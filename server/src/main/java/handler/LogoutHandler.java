package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import request.LoginRequest;
import response.LoginResponse;
import response.LogoutResponse;
import service.ErrorMessages;
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

        try {
            LogoutResponse sum = logoutService.logout(authToken);

            if (sum.message() == null) {
                ctx.status(200);
            } else {
                System.out.println("Culprit!");
                ctx.status(401);
            }

            ctx.json(sum);
        } catch (Exception e) {
            System.err.println("Database error during logout: " + e.getMessage());
            ctx.status(500);
            ctx.json(new LogoutResponse(ErrorMessages.SQLERROR));
        }
    }
}

