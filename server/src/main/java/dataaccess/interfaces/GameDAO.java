package dataaccess.interfaces;

import chess.ChessGame;
import model.GameData;
import response.JoinGameResponse;

import java.util.Collection;

public interface GameDAO {
    void clearApplication(AuthDAO authData, UserDAO userData);
    int createGame(String gameName);
    void clearGames();
    JoinGameResponse updateUserInGame(int gameID, String username, ChessGame.TeamColor teamColor);
    Collection<GameData> listGames();
    boolean isVerifiedGame(int gameID);
    int createGameID();
}
