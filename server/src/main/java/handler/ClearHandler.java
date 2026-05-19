package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import service.ErrorMessages;
import service.SystemService;

import java.util.Map;

public class ClearHandler {

    private final SystemService systemService;

    public ClearHandler(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        systemService = new SystemService(gameDAO, authDAO, userDAO);
    }

    public void clear(Context ctx) {
        try {
            systemService.clearApplication();
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result("{}");
        } catch (Exception e) {
            ctx.status(500);
            // error with ctx.json because I don't have correct dependency for it
            ctx.contentType("application/json");
            ctx.result("{\"message\":\"" + ErrorMessages.SQLERROR + "\"}");
        }
    }
}