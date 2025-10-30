package dataaccess.memory;

import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame;
import model.GameData;
import response.JoinGameResponse;
import service.ErrorMessages;

public class MemoryGame implements GameDAO {
    private Collection<GameData> gameList = new ArrayList<>();
    private int initialGameID;

    public MemoryGame() {
        initialGameID = 1;
    }

    public int createGameID() {
        return initialGameID++;
    }

    public void clearGames() {
        gameList.clear();
    }

    @Override
    public JoinGameResponse updateUserInGame(int gameID, String username, ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            for (GameData dataGame : gameList) {
                if (dataGame.getGameID() == gameID) {
                    if (dataGame.getBlackUsername() == null) {
                        dataGame.setBlackUsername(username);
                        return new JoinGameResponse(null);
                    } else {
                        return new JoinGameResponse(ErrorMessages.ALREADYTAKEN);
                    }
                }
            }
        } else if (teamColor == ChessGame.TeamColor.WHITE) {
            for (GameData dataGame : gameList) {
                if (dataGame.getGameID() == gameID) {
                    if (dataGame.getWhiteUsername() == null) {
                        dataGame.setWhiteUsername(username);
                        return new JoinGameResponse(null);
                    } else {
                        return new JoinGameResponse(ErrorMessages.ALREADYTAKEN);
                    }
                }
            }
        } else {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }
        return new JoinGameResponse(ErrorMessages.UNAUTHORIZED);
    }

    @Override
    public Collection<GameData> listGames() {
        return gameList;
    }

    @Override
    public boolean isVerifiedGame(int gameID) {
        for (GameData gameData: gameList){
            if (gameData.getGameID() == gameID){
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearApplication(AuthDAO authData, UserDAO userData) {
        authData.clearAuths();
        userData.clearUsers();
        this.clearGames();
    }

    @Override
    public int createGame(String gameName) {
        int newGameID = createGameID();
        GameData newGame = new GameData(newGameID, null, null, gameName, new ChessGame());
        gameList.add(newGame);
        return newGameID;
    }
}
