package dataaccess.sqldaos;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;

import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
    }

    @Override
    public void clearAuths() {
        String sql = "TRUNCATE TABLE auth";

        try {
            DatabaseManager.executeUpdate(sql);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to clear auth table", e);
        }
    }

    @Override
    public boolean isVerifiedAuth(String authToken) {
        String sql = "SELECT authToken FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to verify auth token", e);
        }
    }

    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth (username, authToken) VALUES (?, ?)";

        try {
            DatabaseManager.executeUpdate(sql, username, authToken);
            return authToken;
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create auth token", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        String sql = "DELETE FROM auth WHERE authToken = ?";

        try {
            DatabaseManager.executeUpdate(sql, authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to delete auth token", e);
        }
    }

    @Override
    public String getUsernameFromAuth(String authToken) {
        String sql = "SELECT username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
                return null;
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to retrieve username from auth token", e);
        }
    }
}
