package dataaccess.sqldaos;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import model.GameData;
import response.JoinGameResponse;
import service.ErrorMessages;
import translator.Translation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    private int initialGameDAO;

    public SQLGameDAO() {
        initialGameDAO = 1;
    }

    @Override
    public void clearGames() {
        String sql = "TRUNCATE TABLE game";

        try {
            DatabaseManager.executeUpdate(sql);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to clear games table", e);
        }
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<>();

        String sql = "SELECT game_id, white_username, black_username, game_name, game_info FROM game";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                int gameID = rs.getInt("game_id");
                String whiteUsername = rs.getString("white_username");
                String blackUsername = rs.getString("black_username");
                String gameName = rs.getString("game_name");
                String gameInfo = rs.getString("game_info");

                ChessGame game = Translation.fromJsontoObjectNotRequest(gameInfo, ChessGame.class);

                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to list games", e);
        }

        return games;
    }

    @Override
    public JoinGameResponse updateUserInGame(int gameID, String username, ChessGame.TeamColor teamColor) {

        if (teamColor != ChessGame.TeamColor.WHITE &&
                teamColor != ChessGame.TeamColor.BLACK) {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }

        if (!isEmpty(gameID, teamColor)) {
            return new JoinGameResponse(ErrorMessages.ALREADYTAKEN);
        }

        String sql;

        if (teamColor == ChessGame.TeamColor.WHITE) {
            sql = "UPDATE game SET white_username = ? WHERE game_id = ?";
        } else {
            sql = "UPDATE game SET black_username = ? WHERE game_id = ?";
        }

        try {
            DatabaseManager.executeUpdate(sql, username, gameID);
        } catch (DataAccessException e) {
            return new JoinGameResponse(ErrorMessages.SQLERROR);
        }

        return new JoinGameResponse(null);
    }

    private boolean isEmpty(int gameID, ChessGame.TeamColor teamColor) {

        String sql;
        String column;

        if (teamColor == ChessGame.TeamColor.WHITE) {
            sql = "SELECT white_username FROM game WHERE game_id = ?";
            column = "white_username";
        } else {
            sql = "SELECT black_username FROM game WHERE game_id = ?";
            column = "black_username";
        }

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (var rs = stmt.executeQuery()) {

                if (rs.next()) {
                    String username = rs.getString(column);
                    return username == null || username.isEmpty();
                }

                return false;
            }

        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean isVerifiedGame(int gameID) {

        String sql = "SELECT game_id FROM game WHERE game_id = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public int createGame(String gameName) {

        int gameID = createGameID();

        String sql = "INSERT INTO game (game_id, game_name, game_info) VALUES (?, ?, ?)";
        String gameInfo = (String) Translation.fromObjectToJson(new ChessGame());

        try {
            DatabaseManager.executeUpdate(sql, gameID, gameName, gameInfo);
            return gameID;

        } catch (DataAccessException e) {
            return 0;
        }
    }

    @Override
    public int createGameID() {
        return initialGameDAO++;
    }

}
