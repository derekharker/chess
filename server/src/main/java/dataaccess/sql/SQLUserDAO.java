package dataaccess.sqldaos;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
    }

    @Override
    public void clearUsers() {

        String sql = "DELETE FROM user";

        try {
            DatabaseManager.executeUpdate(sql);

        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear users", e);
        }
    }

    @Override
    public boolean userExists(String username) {

        String sql = "SELECT username FROM user WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public UserData createUser(UserData userData) {

        if (userData.password() == null) {
            return new UserData(null, null, null);
        }

        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        try {
            DatabaseManager.executeUpdate(
                    sql,
                    userData.username(),
                    hashedPassword,
                    userData.email()
            );

            return userData;

        } catch (DataAccessException e) {
            return new UserData(null, null, null);
        }
    }

    @Override
    public boolean isVerifiedUser(String username, String password) {

        String sql = "SELECT password FROM user WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (var rs = stmt.executeQuery()) {

                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return BCrypt.checkpw(password, hashedPassword);
                }

                return false;
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to verify user", e);
        }
    }

    @Override
    public UserData getUser(String username) {

        String sql = "SELECT username, password, email FROM user WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (var rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }

                return null;
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to get user", e);
        }
    }
}
