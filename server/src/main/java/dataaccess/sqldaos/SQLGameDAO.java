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


    }
}
