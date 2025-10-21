package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import response.ClearResponse;
import service.SystemService;

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
        SystemService clrService = new SystemService(userDAO, authDAO, gameDAO);
        ClearResponse myResponse = clrService.clearApplication();

        ctx.status(200);
        ctx.json(myResponse);
    }
}
