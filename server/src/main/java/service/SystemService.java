package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import response.ClearResponse;

public class SystemService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public SystemService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public ClearResponse clearApplication() {
        try {
            gameDAO.clearApplication(authDAO, userDAO);
            return new ClearResponse("Clear succeeded");
        } catch (Exception e) {
            System.err.println("Error caught in clearApplication");
            throw new RuntimeException("Database connection failed", e);
        }
    }

}
