package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.JoinGameResponse;
import response.ListGamesResponse;

import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) {
        if (!authDAO.isVerifiedAuth(createGameRequest.authToken())) {
            return new CreateGameResponse(null, ErrorMessages.UNAUTHORIZED);
        }
        int gamID = gameDAO.createGame(createGameRequest.gameName());
        if (gamID == 0) {
            return new CreateGameResponse(null, ErrorMessages.SQLERROR);
        }
        return new CreateGameResponse(gamID, null);
    }

    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) {

        if (!authDAO.isVerifiedAuth(joinGameRequest.authToken())) {
            System.out.println("Unauthorized game service");
            return new JoinGameResponse(ErrorMessages.UNAUTHORIZED);
        }

        if (!gameDAO.isVerifiedGame(joinGameRequest.gameID())) {
            System.out.println("Bad request game service");
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }
//        return new JoinGameResponse(null);
        return gameDAO.updateUserInGame(joinGameRequest.gameID(),
                    authDAO.getUsernameFromAuth(joinGameRequest.authToken()), joinGameRequest.playerColor());
    }

    public ListGamesResponse listGames(String authT) {
        if (!authDAO.isVerifiedAuth(authT)) {
            return new ListGamesResponse(null, ErrorMessages.UNAUTHORIZED);
        }
        return new ListGamesResponse(gameDAO.listGames(), null); //List all games if good
    }
}
