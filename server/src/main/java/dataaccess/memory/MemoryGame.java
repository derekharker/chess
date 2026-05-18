package dataaccess.memory;

import chess.ChessGame;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import model.GameData;
import response.JoinGameResponse;
import service.ErrorMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGame implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public MemoryGame() {
    }

    private int generateID() {
        return nextGameID++;
    }

    public int createGameID() {
        return nextGameID++;
    }

    public void clearGames() {
        games.clear();
    }

    @Override
    public JoinGameResponse updateUserInGame(int gameID, String username, ChessGame.TeamColor teamColor) {

        GameData game = games.get(gameID);

        if (game == null) {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }

        if (teamColor == null) {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }

        if (teamColor == ChessGame.TeamColor.WHITE) {

            if (game.getWhiteUsername() != null) {
                return new JoinGameResponse(ErrorMessages.ALREADYTAKEN);
            }

            game.setWhiteUsername(username);
            return new JoinGameResponse(null);
        }

        if (teamColor == ChessGame.TeamColor.BLACK) {

            if (game.getBlackUsername() != null) {
                return new JoinGameResponse(ErrorMessages.ALREADYTAKEN);
            }

            game.setBlackUsername(username);
            return new JoinGameResponse(null);
        }

        return new JoinGameResponse(ErrorMessages.BADREQUEST);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public boolean isVerifiedGame(int gameID) {
        return games.containsKey(gameID);
    }

    @Override
    public void clearApplication(AuthDAO authData, UserDAO userData) {
        authData.clearAuths();
        userData.clearUsers();
        clearGames();
    }

    @Override
    public int createGame(String gameName) {

        int id = generateID();

        GameData game = new GameData(
                id,
                null,
                null,
                gameName,
                new ChessGame()
        );

        games.put(id, game);

        return id;
    }
}
