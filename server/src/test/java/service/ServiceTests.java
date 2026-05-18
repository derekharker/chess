package service;

import chess.ChessGame;
import dataaccess.memory.MemoryAuth;
import dataaccess.memory.MemoryGame;
import dataaccess.memory.MemoryUser;
import org.junit.jupiter.api.*;
import request.*;
import response.*;

public class ServiceTests {

    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "testEmail";
    private static final String GAME_NAME = "testGame";

    private final MemoryGame gameDAO = new MemoryGame();
    private final MemoryAuth authDAO = new MemoryAuth();
    private final MemoryUser userDAO = new MemoryUser();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(authDAO, gameDAO);
    private final SystemService systemService = new SystemService(gameDAO, authDAO, userDAO);

    @BeforeEach
    void setup() {
        systemService.clearApplication();
    }

    /*
     * User Service Tests
     */

    @Test
    @DisplayName("Register Success")
    void registerSuccess() {
        RegisterResponse response = userService.register(buildRegisterRequest());
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    @DisplayName("Register No Email Failure")
    void registerFailureMissingEmail() {
        RegisterRequest request = new RegisterRequest(USERNAME, PASSWORD, null);
        RegisterResponse response = userService.register(request);
        Assertions.assertEquals(ErrorMessages.BADREQUEST, response.message());
    }

    @Test
    @DisplayName("Register Duplicate Failure")
    void registerDuplicateFailure() {
        registerUser();
        RegisterResponse response = userService.register(buildRegisterRequest());
        Assertions.assertEquals(ErrorMessages.ALREADYTAKEN, response.message());
    }

    @Test
    @DisplayName("Login Success")
    void loginSuccess() {
        registerThenLogout();
        LoginResponse response = userService.login(new LoginRequest(USERNAME, PASSWORD));
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    @DisplayName("Login Failure")
    void loginFailure() {
        registerThenLogout();
        LoginResponse response = userService.login(new LoginRequest(USERNAME, "Incorrect Password"));
        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, response.message());
    }

    @Test
    @DisplayName("Logout Success")
    void logoutSuccess() {
        LogoutResponse response = registerThenLogout();
        Assertions.assertNull(response.message());
    }

    @Test
    @DisplayName("Logout Failure")
    void logoutFailure() {
        registerUser();
        LogoutResponse response = userService.logout("Incorrect Auth");
        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, response.message());
    }

    /*
     * Game Service Tests
     */

    @Test
    @DisplayName("Create Game Success")
    void createGameSuccess() {
        CreateGameResponse response = createGame();
        Assertions.assertNull(response.message());
    }

    @Test
    @DisplayName("Create Game Failure")
    void createGameFailure() {
        CreateGameResponse response = gameService.createGame(new CreateGameRequest(GAME_NAME, "False Auth"));
        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, response.message());
    }

    @Test
    @DisplayName("Join Game Success")
    void joinGameSuccess() {
        RegisterResponse user = registerUser();
        CreateGameResponse game = gameService.createGame(new CreateGameRequest(GAME_NAME, user.authToken()));

        JoinGameResponse response = gameService.joinGame(
                new JoinGameRequest(ChessGame.TeamColor.WHITE, game.gameID(), user.authToken()));

        Assertions.assertNull(response.message());
    }

    @Test
    @DisplayName("Join Game Failure")
    void joinGameFailure() {
        RegisterResponse user = registerUser();
        CreateGameResponse game = gameService.createGame(new CreateGameRequest(GAME_NAME, user.authToken()));
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, game.gameID(), user.authToken());
        gameService.joinGame(request);

        JoinGameResponse secondAttempt = gameService.joinGame(request);
        Assertions.assertEquals(ErrorMessages.ALREADYTAKEN, secondAttempt.message());
    }

    @Test
    @DisplayName("Join Invalid Game Failure")
    void joinInvalidGameFailure() {
        RegisterResponse user = registerUser();
        JoinGameResponse response =
                gameService.joinGame(
                        new JoinGameRequest(ChessGame.TeamColor.WHITE, 9999, user.authToken())
                );
        Assertions.assertEquals(
                ErrorMessages.BADREQUEST,
                response.message()
        );
    }

    @Test
    @DisplayName("List Games Success")
    void listGamesSuccess() {
        RegisterResponse user = createUserAndGame();
        ListGamesResponse response = gameService.listGames(user.authToken());
        Assertions.assertNotNull(response.games());
    }

    @Test
    @DisplayName("List Games Failure")
    void listGamesFailure() {
        createUserAndGame();
        ListGamesResponse response = gameService.listGames("False Auth");
        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, response.message());
    }

    /*
     * System Tests
     */

    @Test
    @DisplayName("Clear Database Success")
    void clearDatabaseSuccess() {
        RegisterResponse user = createUserAndGame();

        systemService.clearApplication();

        ListGamesResponse response = gameService.listGames(user.authToken());

        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, response.message());
    }

    /*
     * Helper Methods
     */

    private RegisterRequest buildRegisterRequest() {
        return new RegisterRequest(USERNAME, PASSWORD, EMAIL);
    }

    private RegisterResponse registerUser() {
        return userService.register(buildRegisterRequest());
    }

    private LogoutResponse registerThenLogout() {
        RegisterResponse user = registerUser();
        return userService.logout(user.authToken());
    }

    private CreateGameResponse createGame() {
        RegisterResponse user = registerUser();
        return gameService.createGame(new CreateGameRequest(GAME_NAME, user.authToken()));
    }

    private RegisterResponse createUserAndGame() {
        RegisterResponse user = registerUser();
        gameService.createGame(new CreateGameRequest(GAME_NAME, user.authToken()));
        return user;
    }
}
