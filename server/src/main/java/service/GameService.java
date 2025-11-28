package service;

import chess.ChessGame;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import request.*;
import response.*;

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
            return new JoinGameResponse(ErrorMessages.UNAUTHORIZED);
        }

        if (!gameDAO.isVerifiedGame(joinGameRequest.gameID())) {
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

    public LeaveGameResponse leaveGame(LeaveGameRequest request) {
        if (!authDAO.isVerifiedAuth(request.authToken())) {
            return new LeaveGameResponse(ErrorMessages.UNAUTHORIZED);
        }
        if (!gameDAO.isVerifiedGame(request.gameID())) {
            return new LeaveGameResponse(ErrorMessages.BADREQUEST);
        }
        return new LeaveGameResponse(gameDAO.updateUserInGame(request.gameID(), null, request.teamColor()).message());
    }

    public LeaveGameResponse resignGame(ResignGameRequest request) {
        if (!authDAO.isVerifiedAuth(request.authToken())) {
            return new LeaveGameResponse(ErrorMessages.UNAUTHORIZED);
        }
        if (!gameDAO.isVerifiedGame(request.gameID())) {
            return new LeaveGameResponse(ErrorMessages.BADREQUEST);
        }
        ChessGame tempGame = gameDAO.getGame(request.gameID());
        if (tempGame.isGameOver()){
            return new LeaveGameResponse("Game is already over. Cannot Resign");
        }
        tempGame.setGameOver(true);

        gameDAO.updateGame(request.gameID(), tempGame);

        return new LeaveGameResponse(null);
    }

    public GetGameResponse returnGame(GetGameRequest request){
        if (!authDAO.isVerifiedAuth(request.authToken())) {return new GetGameResponse(null, ErrorMessages.UNAUTHORIZED);}
        if (!gameDAO.isVerifiedGame(request.gameID())) {
            return new GetGameResponse(null, ErrorMessages.BADREQUEST);
        }

        return new GetGameResponse(gameDAO.getGame(request.gameID()), null);
    }


}
