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
        System.out.println("Auth token is: " + createGameRequest.authToken());
        if (createGameRequest.gameName() == null) {
            return new CreateGameResponse(null, ErrorMessages.BADREQUEST);
        }

        if (!authDAO.isVerifiedAuth(createGameRequest.authToken())) {
            return new CreateGameResponse(null, ErrorMessages.UNAUTHORIZED);
        }
        int gamID = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResponse(gamID, null);
    }

    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) {
        if (!authDAO.isVerifiedAuth(joinGameRequest.authToken())) {
            return new JoinGameResponse(ErrorMessages.UNAUTHORIZED);
        }

        if (!gameDAO.isVerifiedGame(joinGameRequest.gameID())) {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }
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
