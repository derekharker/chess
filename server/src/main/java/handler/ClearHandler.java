package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import response.ClearResponse;
import service.SystemService;

import java.sql.SQLException;

public class ClearHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void handle(Context ctx) {
        SystemService clrService = new SystemService(gameDAO, authDAO, userDAO);
        try {
            ClearResponse response = clrService.clearApplication();
            ctx.status(200);
            ctx.json(response);
        } catch (Exception e) {
            System.err.println("ClearHandler caught error: " + e.getMessage());
            ctx.status(500);
            ctx.json(new ClearResponse("Database connection Error"));
        }
    }
}
