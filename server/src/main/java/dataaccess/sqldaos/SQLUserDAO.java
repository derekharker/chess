package dataaccess.sqldaos;


import org.mindrot.jbcrypt.BCrypt;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLUserDAO {

    public SQLUserDAO() {

    }

    @Override
    void clearUsers() {
        var st = "DELETE FROM user";
        try {
            executeUpdate(st);
        } catch (DataAccessException e) {
            System.out.println("Error in clearUsers(): " + e.getMessage());
        }
    }

    @Override
    public boolean userExists(String username) {
        String st = "SELECT COUNT(*) FROM user WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(st)) {
            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    return cnt > 0;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in SQL userExists: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UserData createUser(UserData userData) {
        if (userData.password() == null) {
            return new UserData(null, null, null);
        }
        var st = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        String hashPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        try {
            var id = executeUpdate(st, userData.username(), hashPassword, userData.email());
        } catch (DataAccessException e) {
            System.out.println("Problem in creating user: " + e.getMessage());
            return new UserData(null, null, null);
        }
        return userData;
    }

    @Override
    public boolean isVerifiedUser(String username, String password) {
        String st = "SELECT password FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(st)) {
            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashPassword = rs.getString("password");
                    return BCrypt.checkpw(password, hashPassword);
                } else {
                    return false;
                }
            }
        } catch (DataAccessException | SQLException ex) {
            System.out.println("Error in verifying user: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
