package service;

import chess.ChessGame;
import dataaccess.memory.MemoryAuth;
import dataaccess.memory.MemoryGame;
import dataaccess.memory.MemoryUser;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.*;

public class ServiceTests {
    String testUsername = "testUsername";
    String testPassword = "testPassword";
    String testEmail = "testEmail";
    String testGame = "testGame";

    MemoryGame gameDAO = new MemoryGame();
    MemoryAuth authDAO = new MemoryAuth();
    MemoryUser userDAO = new MemoryUser();
    UserService userService = new UserService(authDAO, userDAO);
    GameService gameService = new GameService(authDAO, gameDAO);
    SystemService systemService = new SystemService(gameDAO, authDAO, userDAO);


    @Test
    @BeforeEach
    @DisplayName("Clearing the Database")
    void resetServer(){
        systemService.clearApplication();
    }

    /**
     * User service.Service Tests
     */

    @Test
    @DisplayName("Register Success")
    void registerSuccess(){
        RegisterRequest regRequest = new RegisterRequest(testUsername, testPassword, testEmail);
        UserService registerService =  new UserService(authDAO, userDAO);
        RegisterResponse actualResponse = registerService.register(regRequest);

        Assertions.assertNotNull(actualResponse.authToken(), "No auth token given");
    }

    @Test
    @DisplayName("Register No Email Failure")
    void registerEmailFailure(){
        RegisterRequest regRequest = new RegisterRequest(testUsername, testPassword, null);
        UserService registerService =  new UserService(authDAO, userDAO);
        RegisterResponse actualResponse = registerService.register(regRequest);

        Assertions.assertSame(actualResponse.msg(), ErrorMessages.BADREQUEST);
    }

    @Test
    @DisplayName("Login Success")
    void loginSuccess(){
        registerAndLogoutUser();
        LoginResponse loginResponse = userService.login(new LoginRequest(testUsername, testPassword));

        Assertions.assertNotNull(loginResponse.authToken());
    }

    @Test
    @DisplayName("Login Password Failure")
    void loginFailure() {
        registerAndLogoutUser();
        LoginResponse loginResponse = userService.login(new LoginRequest(testUsername, "Incorrect Password"));
        Assertions.assertSame(ErrorMessages.UNAUTHORIZED, loginResponse.authToken());
    }

    @Test
    @DisplayName("logout Success")
    void logoutSuccess(){
        LogoutResponse logoutResponse = registerAndLogoutUser();
        Assertions.assertNull(logoutResponse.msg());
    }

    @Test
    @DisplayName("Logout Bad Auth Token Failure")
    void logoutFailure(){
        RegisterResponse registerRequest = registerTestUser();
        LogoutResponse logoutResponse = userService.logout("Incorrect Auth");

        Assertions.assertSame(ErrorMessages.UNAUTHORIZED, logoutResponse.msg());
    }

    /**
     * GameService Tests
     */

    @Test
    @DisplayName("Create Game Success")
    void createGameSuccess(){
        CreateGameResponse createGameResponse = createUserAndGameReturnGame();
        Assertions.assertNull(createGameResponse.msg());
    }

    @Test
    @DisplayName("Create Game Bad Auth Failure")
    void createGameFailure(){
        CreateGameResponse createGameResponse = gameService.createGame(new CreateGameRequest(testGame, "False Auth"));
        Assertions.assertSame(ErrorMessages.UNAUTHORIZED, createGameResponse.msg());
    }

    @Test
    @DisplayName("Join Game Success")
    void joinGameSuccess(){
        RegisterResponse registerResponse = registerTestUser();
        CreateGameResponse createGameResponse = gameService.createGame(new CreateGameRequest(testGame, registerResponse.authToken()));
        JoinGameResponse joinGameResponse = gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE,
                createGameResponse.gameID(), registerResponse.authToken()));

        Assertions.assertNull(joinGameResponse.msg());
    }

    @Test
    @DisplayName("Join User Position in Game Failure")
    void joinGameFailure(){
        RegisterResponse registerResponse = registerTestUser();
        CreateGameResponse createGameResponse = gameService.createGame(new CreateGameRequest(testGame, registerResponse.authToken()));

        JoinGameRequest testJoinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE,
                createGameResponse.gameID(), registerResponse.authToken());

        JoinGameResponse joinGameResponse = gameService.joinGame(testJoinGameRequest);
        JoinGameResponse secondJoinGameResponse = gameService.joinGame(testJoinGameRequest);

        Assertions.assertSame(ErrorMessages.ALREADYTAKEN, secondJoinGameResponse.msg());
    }

    @Test
    @DisplayName("ListGamesSuccess")
    void listGamesSuccess(){
        RegisterResponse registerResponse = createUserAndGameReturnUser();
        ListGamesResponse listGamesResponse = gameService.listGames(registerResponse.authToken());

        Assertions.assertNotNull(listGamesResponse.games());

    }

    @Test
    @DisplayName("List Games Authentication Failure")
    void listGamesFailure(){
        RegisterResponse registerResponse = createUserAndGameReturnUser();
        ListGamesResponse listGamesResponse = gameService.listGames("False Auth");

        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, listGamesResponse.msg());

    }

    /**
     * System service.Service Tests
     */
    @Test
    @DisplayName("Clear Database Success")
    void clearDatabaseSuccess(){
        RegisterResponse registerResponse = createUserAndGameReturnUser();
        systemService.clearApplication();

        ListGamesResponse listGamesResponse = gameService.listGames(registerResponse.authToken());

        Assertions.assertEquals(ErrorMessages.UNAUTHORIZED, listGamesResponse.msg());
    }


    /**
     *Helper Functions
     */
    RegisterResponse registerTestUser(){
        return userService.register(new RegisterRequest(testUsername, testPassword, testEmail));
    }

    LogoutResponse registerAndLogoutUser(){
        return userService.logout(userService.register(new RegisterRequest(testUsername, testPassword, testEmail)).authToken());
    }

    CreateGameResponse createUserAndGameReturnGame() {
        RegisterResponse registerResponse = registerTestUser();
        return gameService.createGame(new CreateGameRequest(testGame, registerResponse.authToken()));
    }

    RegisterResponse createUserAndGameReturnUser(){
        RegisterResponse registerResponse = registerTestUser();
        gameService.createGame(new CreateGameRequest(testGame, registerResponse.authToken()));
        return registerResponse;
    }
}
