package dataaccess.sqldaos;

import dataaccess.DataAccessException;

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
}
