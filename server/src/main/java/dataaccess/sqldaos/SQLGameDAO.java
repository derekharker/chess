package dataaccess.sqldaos;

import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;

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

    }
}
