package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import response.ClearResponse;
import service.SystemService;

public class ClearHandler {

    private final SystemService systemService;

    public ClearHandler(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        systemService = new SystemService(gameDAO, authDAO, userDAO);
    }

    public void clear(Context ctx) {

        ClearResponse response = systemService.clearApplication();

        ctx.status(200);
        ctx.json(response);
    }
}
