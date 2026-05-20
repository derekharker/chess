package dataaccess;

import chess.ChessGame;
import dataaccess.sqldaos.SQLAuthDAO;
import dataaccess.sqldaos.SQLGameDAO;
import dataaccess.sqldaos.SQLUserDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import response.JoinGameResponse;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SQL DAO Integration Tests")
public class SQLTests {

    private final SQLGameDAO gameDAO = new SQLGameDAO();
    private final SQLAuthDAO authDAO = new SQLAuthDAO();
    private final SQLUserDAO userDAO = new SQLUserDAO();

    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "test@email.com";
    private static final String GAME_NAME = "Sample Game";

    private UserData testUser;

    @BeforeEach
    void setup() {
        gameDAO.clearApplication(authDAO, userDAO);
        testUser = new UserData(USERNAME, PASSWORD, EMAIL);
    }

    // ============================================================
    // User DAO
    // ============================================================

    @Nested
    @DisplayName("User DAO")
    class UserDAOTests {

        @Test
        void createUserStoresValidUser() {
            UserData created = userDAO.createUser(testUser);

            assertEquals(testUser, created);
        }

        @Test
        void createUserRejectsMissingPassword() {
            UserData invalid = new UserData(USERNAME, null, EMAIL);

            UserData result = userDAO.createUser(invalid);

            assertNull(result.username());
        }

        @Test
        void existingUserCanBeFound() {
            registerUser();

            assertNotNull(userDAO.getUser(USERNAME));
        }

        @Test
        void nonexistentUserReturnsFalse() {
            assertNull(userDAO.getUser(USERNAME));
        }

        @Test
        void verifiedCredentialsReturnTrue() {
            registerUser();

            assertTrue(userDAO.isVerifiedUser(USERNAME, PASSWORD));
        }

        @Test
        void invalidCredentialsReturnFalse() {
            assertFalse(userDAO.isVerifiedUser(USERNAME, PASSWORD));
        }

        @Test
        void clearUsersRemovesStoredUsers() {
            registerUser();
            userDAO.clearUsers();
            assertNull(userDAO.getUser(USERNAME));
        }
    }

    // ============================================================
    // Auth DAO
    // ============================================================

    @Nested
    @DisplayName("Auth DAO")
    class AuthDAOTests {

        @Test
        void authTokenIsGenerated() {
            String token = authDAO.createAuth(USERNAME);

            assertNotNull(token);
        }

        @Test
        void nullUsernameThrowsException() {
            assertThrows(
                    RuntimeException.class,
                    () -> authDAO.createAuth(null)
            );
        }

        @Test
        void createdTokenIsValid() {
            String token = authDAO.createAuth(USERNAME);

            assertTrue(authDAO.isVerifiedAuth(token));
        }

        @Test
        void invalidTokenFailsVerification() {
            assertFalse(authDAO.isVerifiedAuth("bad-token"));
        }

        @Test
        void usernameCanBeRecoveredFromToken() {
            String token = authDAO.createAuth(USERNAME);

            assertEquals(USERNAME, authDAO.getUsernameFromAuth(token));
        }

        @Test
        void invalidTokenDoesNotReturnCorrectUser() {
            authDAO.createAuth(USERNAME);

            String result = authDAO.getUsernameFromAuth("fake");

            assertNotEquals(USERNAME, result);
        }

        @Test
        void deletingTokenInvalidatesIt() {
            String token = authDAO.createAuth(USERNAME);

            authDAO.deleteAuth(token);

            assertFalse(authDAO.isVerifiedAuth(token));
        }

        @Test
        void deletingWrongTokenDoesNothing() {
            String token = authDAO.createAuth(USERNAME);

            authDAO.deleteAuth("wrong-token");

            assertTrue(authDAO.isVerifiedAuth(token));
        }

        @Test
        void clearAuthsRemovesAllTokens() {
            String token = authDAO.createAuth(USERNAME);

            authDAO.clearAuths();

            assertFalse(authDAO.isVerifiedAuth(token));
        }
    }

    // ============================================================
    // Game DAO
    // ============================================================

    @Nested
    @DisplayName("Game DAO")
    class GameDAOTests {

        @Test
        void gamesReceiveIncrementingIds() {
            int first = gameDAO.createGame("Game One");
            int second = gameDAO.createGame("Game Two");

            assertAll(
                    () -> assertEquals(1, first),
                    () -> assertEquals(2, second)
            );
        }

        @Test
        void nullGameNameReturnsInvalidId() {
            assertEquals(0, gameDAO.createGame(null));
        }

        @Test
        void createdGameCanBeVerified() {
            int gameId = gameDAO.createGame(GAME_NAME);

            assertTrue(gameDAO.isVerifiedGame(gameId));
        }

        @Test
        void invalidGameIdFailsVerification() {
            gameDAO.createGame(GAME_NAME);

            assertFalse(gameDAO.isVerifiedGame(999));
        }

        @Test
        void listGamesReturnsStoredGames() {
            gameDAO.createGame(GAME_NAME);

            Collection<GameData> games = gameDAO.listGames();

            assertFalse(games.isEmpty());
        }

        @Test
        void emptyDatabaseReturnsEmptyGameList() {
            assertTrue(gameDAO.listGames().isEmpty());
        }

        @Test
        void playerCanJoinOpenColorSlot() {
            int gameId = gameDAO.createGame(GAME_NAME);
            registerUser();

            JoinGameResponse response =
                    gameDAO.updateUserInGame(gameId, USERNAME, ChessGame.TeamColor.WHITE);

            assertNull(response.message());
        }

        @Test
        void occupiedColorSlotCannotBeReused() {
            int gameId = gameDAO.createGame(GAME_NAME);
            registerUser();

            gameDAO.updateUserInGame(gameId, USERNAME, ChessGame.TeamColor.WHITE);

            JoinGameResponse response =
                    gameDAO.updateUserInGame(gameId, USERNAME, ChessGame.TeamColor.WHITE);

            assertNotNull(response.message());
        }

        @Test
        void clearGamesRemovesStoredGames() {
            gameDAO.createGame(GAME_NAME);

            gameDAO.clearGames();

            assertTrue(gameDAO.listGames().isEmpty());
        }
    }

    // ============================================================
    // Integration
    // ============================================================

    @Test
    @DisplayName("Clearing application wipes all persisted data")
    void clearApplicationRemovesEverything() {

        registerUser();

        String token = authDAO.createAuth(USERNAME);
        gameDAO.createGame(GAME_NAME);

        gameDAO.clearApplication(authDAO, userDAO);

        assertAll(
                () -> assertNull(userDAO.getUser(USERNAME)),
                () -> assertFalse(authDAO.isVerifiedAuth(token)),
                () -> assertTrue(gameDAO.listGames().isEmpty())
        );
    }

    // ============================================================
    // Helpers
    // ============================================================

    private void registerUser() {
        userDAO.createUser(testUser);
    }
}