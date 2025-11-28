package dataaccess;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.Properties;

import static java.sql.Types.NULL;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            System.out.println("Get connection error!");
            // Here ?
            ex.printStackTrace();
            throw new DataAccessException("Database connection failed", ex);
        }
    }

    public static int executeUpdate(String statement, Object... parameters) throws DataAccessException{
        try (var connection = DatabaseManager.getConnection()) {
            try (var ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < parameters.length; i++) {
                    var param = parameters[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Problem in Database Manager executing an update: " + e.getMessage() + e);
        }
    }

    private static final String[] CREATEGAMETABLE = {
            """
    CREATE TABLE IF NOT EXISTS `game` (
    `game_id` INT NOT NULL PRIMARY KEY,
    `game_name` VARCHAR(255) NOT NULL,
    `white_username` VARCHAR(256),
    `black_username` VARCHAR(256),
    `game_info` LONGTEXT NOT NULL,
    FOREIGN KEY (`white_username`) REFERENCES `user`(`username`),
    FOREIGN KEY (`black_username`) REFERENCES `user`(`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    private static final String[] CREATEUSERTABLE = {
            """
    CREATE TABLE IF NOT EXISTS `user` (
      `username` varchar(256) NOT NULL,
      `password` varchar(256) NOT NULL,
      `email` varchar(256) NOT NULL,
      PRIMARY KEY (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    private static final String[] CREATEAUTHTABLE = {
            """
    CREATE TABLE IF NOT EXISTS `auth` (
      `id` int AUTO_INCREMENT,
      `username` varchar(256) NOT NULL,
      `authToken` varchar(256),
      PRIMARY KEY (`id`),
      INDEX (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        System.out.println("Created the database");
        try (var connection = DatabaseManager.getConnection()) {
            for (var st : CREATEUSERTABLE) {
                try (var prepSt = connection.prepareStatement(st)) {
                    prepSt.executeUpdate();
                }
            }
            System.out.println("User Table");
            for (var st : CREATEAUTHTABLE) {
                try (var prepSt = connection.prepareStatement(st)) {
                    prepSt.executeUpdate();
                }
            }
            System.out.println("Auth Table");
            for (var st : CREATEGAMETABLE) {
                try (var prepSt = connection.prepareStatement(st)) {
                    prepSt.executeUpdate();
                }
            }
            System.out.println("Game Table");
        } catch (SQLException e) {
            throw new DataAccessException("User, Auth, or Game Table not working" + e.getMessage());
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
