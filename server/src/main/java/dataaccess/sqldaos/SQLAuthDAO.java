package dataaccess.sqldaos;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;

import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() {

    }

    @Override
    public void clearAuths() {
        var st = "TRUNCATE auth";
        try {
            executeUpdate(st);

        } catch (DataAccessException e) {
            System.out.println("Error clearing table" + e.getMessage());
        }
    }

    @Override
    public boolean isVerifiedAuth(String authToken) {
        String st = "SELECT COUNT(*) FROM auth WHERE authToken = ?";

        try (var connection = DatabaseManager.getConnection()) {
            var ps = connection.prepareStatement(st);
            ps.setString(1, authToken);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    return cnt > 0;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Access Denied: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } catch (DataAccessException ex) {
            System.out.println("Data access error: " + ex.getMessage());
        }
        return false;
    }

    @Override
    public String createAuth(String username) {
        String newAuthTok = UUID.randomUUID().toString();
        String st = "INSERT INTO auth (username, authToken) VALUES (?, ?)";

        try {
            executeUpdate(st, username, newAuthTok);
        } catch (DataAccessException e) {
            System.out.println("Error in creating Auth: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return newAuthTok;
    }

    @Override
    public String
}
