package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import request.LoginRequest;
import response.LoginResponse;
import response.LogoutResponse;
import service.UserService;
import translator.Translation;

public class LogoutHandler {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LogoutHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void handle(Context ctx) {

        String authToken = ctx.header("Authorization");

        UserService logoutService = new UserService(userDAO, authDAO);
        LogoutResponse sum = logoutService.logout(authToken);

        if (sum.message() == null) {
            ctx.status(200);
        } else {
            ctx.status(401);
        }

        ctx.json(sum);

    }
}

