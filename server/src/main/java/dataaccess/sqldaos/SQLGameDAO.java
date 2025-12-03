package dataaccess.sqldaos;

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

import chess.ChessGame;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLGameDAO implements GameDAO {
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
    public ChessGame getGame(int gameID){
        String statement = "SELECT game_info FROM game WHERE game_id = ?";
        ChessGame game = null;

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String gameInfo = rs.getString("game_info");
                    game = Translation.fromJsontoObjectNotRequest(gameInfo, ChessGame.class);
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("Error getting the game with ID " + gameID + ": " + e.getMessage());
            e.printStackTrace();
        }

        return game;
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<>();
        String st = "SELECT game_id, white_username, black_username, game_name, game_info FROM game";

        try (var connection = DatabaseManager.getConnection();
            var ps = connection.prepareStatement(st);
            var rs = ps.executeQuery()) {
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

        } catch (SQLException | DataAccessException ex) {
            System.out.println("Error listing out games: " + ex.getMessage());
            ex.printStackTrace();
            return games;
        }
        return games;
    }

    @Override
    public JoinGameResponse updateUserInGame(int gameID, String username, ChessGame.TeamColor teamColor) {
        if (!(teamColor == ChessGame.TeamColor.WHITE || teamColor == ChessGame.TeamColor.BLACK)) {
            return new JoinGameResponse(ErrorMessages.BADREQUEST);
        }

        if (username != null && !isEmpty(gameID, teamColor)) {
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

        try (var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(st)) {

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
        } catch (SQLException | DataAccessException e) {
            System.out.println("SQL Error in isEmpty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isVerifiedGame(int gameID) {
        String st = "SELECT COUNT(*) FROM game WHERE game_id = ?";

        try (var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(st)) {

            ps.setString(1, String.valueOf(gameID));

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    return cnt > 0;
                }
            }

        } catch (SQLException | DataAccessException e) {
            System.out.println("Game not verified. Denied: " + e.getMessage());
            return false;
        }

        return false;
    }

    @Override
    public int createGame(String gameName) {
        var st = "INSERT INTO game (game_id, game_name, game_info) VALUES (?, ?, ?)";
        int gameID = createGameID();
        String gameString = (String) Translation.fromObjectToJson(new ChessGame());

        try {
            executeUpdate(st, gameID, gameName, gameString);
            return gameID;
        } catch (DataAccessException ex) {
            System.out.println("Error in createGame: " + ex.getMessage());
            return 0;
        }
    }

    @Override
    public int createGameID(){
        return initialGameDAO++;
    }

    @Override
    public ChessGame.TeamColor getTeamColor(int gameID, String username) {
        String statement = "SELECT white_username, black_username FROM game WHERE game_id = ?";
        ChessGame.TeamColor teamColor = null;

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String whiteUsername = rs.getString("white_username");
                    String blackUsername = rs.getString("black_username");

                    if (username.equals(whiteUsername)) {
                        teamColor = ChessGame.TeamColor.WHITE;
                    } else if (username.equals(blackUsername)) {
                        teamColor = ChessGame.TeamColor.BLACK;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("Error getting team color for username " + username + " and gameID " + gameID + ": " + e.getMessage());
            e.printStackTrace();
        }

        return teamColor;
    }

    @Override
    public void clearApplication(AuthDAO authDAO, UserDAO userDAO) {
        try {
            clearGames();
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (Exception e) {
            System.err.println("Error in clearApplication: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @Override
    public boolean updateGame(int gameID, ChessGame game){
        String statement= "UPDATE game SET game_info = ? WHERE game_id = ?";
        String gameJson = (String) Translation.fromObjectToJson(game);
        try{
            executeUpdate(statement, gameJson, gameID);
            return true;
        }catch(DataAccessException e){
            System.out.println("Error updating a game:" + e.getMessage());
            return false;
        }
    }
}
