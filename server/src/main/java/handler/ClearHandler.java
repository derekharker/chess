package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import service.SystemService;

public class ClearHandler {

    private final SystemService systemService;

    public ClearHandler(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        systemService = new SystemService(gameDAO, authDAO, userDAO);
    }

    public void clear(Context ctx) {
        systemService.clearApplication();
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }
}
