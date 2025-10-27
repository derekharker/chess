package dataaccess.sqldaos;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import response.JoinGameResponse;
import service.ErrorMessages;
import translator.Translation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLGameDAO {
    private int initialGameDAO;

    public SQLGameDAO() {
        initialGameDAO = 1;
    }

    @Override
    public void clearGames() {
        var st = "TRUNCATE game";
        try {
            executeUpdate(st);
        } catch (DataAccessException ex) {
            System.out.println("Clear games error" + ex.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<>();
        String st = "SELECT game_id, white_username, black_username, game_name, game_info FROM game";

        try (var connection = DatabaseManager.getConnection()) {
            var ps = connection.prepareStatement(st);
            var rs = ps.executeQuery(); {
                while (rs.next()) {
                    int gameID = rs.getInt("game_id");
                    String whiteUser = rs.getString("white_username");
                    String blackUser = rs.getString("black_username");
                    String gameName = rs.getString("game_name");
                    String gameInfo = rs.getString("game_info");

                    ChessGame game = Translation.fromJsontoObjectNotRequest(gameInfo, ChessGame.class);

                    GameData gameData = new GameData(gameID, whiteUser, blackUser, gameName, game);
                    games.add(gameData);
                }
            }

        } catch (SQLException | DataAccessException ex) {
            System.out.println("Error listing out games: " + ex.getMessage());
            ex.printStackTrace();
            return games;
        }
        return games;
    }

    @Override
    public JoinGameResponse updateUserInGame(int gameID, String username, ChessGame.TeamColor teamColor) {
        if (!(teamColor == ChessGame.TeamColor.WHITE || teamColor == ChessGame.TeamColor.BLACK) {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }
        if (!isEmpty(gameID, teamColor)) {
            return new JoinGameResponse(ErrorMessages.ALREADYTAKEN);
        }

        String st;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            st = "UPDATE game SET white_username = ? WHERE game_id = ?";
        } else {
            st = "UPDATE game SET black_username = ? WHERE game_id = ?";
        }

        try {
            executeUpdate(st, username, gameID);
        } catch (DataAccessException e) {
            System.out.println("Error updating user in game: " + e.getMessage());
            return new JoinGameResponse(ErrorMessages.SQLERROR);
        }

        return new JoinGameResponse(null);
    }

    private boolean isEmpty(int gameID, ChessGame.TeamColor teamColor) {
        String userType;
        String st;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            userType = "white_username";
            st = "SELECT white_username FROM game WHERE game_id = ?";
        } else {
            userType = "black_username";
            st = "SELECT black_username FROM game WHERE game_id = ?";
        }

        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(st); {
                ps.setInt(1, gameID);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString(userType);
                        boolean result = username == null || username.isEmpty();
                        return result;
                    } else {
                        System.out.println("Game does not exist.");
                        return false;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("SQL Error in isEmpty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
