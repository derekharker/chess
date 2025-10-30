package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import org.eclipse.jetty.server.Authentication;
import request.LoginRequest;
import response.LoginResponse;
import service.ErrorMessages;
import service.UserService;
import translator.Translation;

public class LoginHandler {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void handle(Context ctx) {
        LoginRequest loginRequest = (LoginRequest) Translation.fromJsonToObject(ctx, LoginRequest.class);

        UserService loginService = new UserService(userDAO, authDAO);
        try {
            LoginResponse response = loginService.login(loginRequest);

            if (response.username() == null) {
                ctx.status(401); // bad credentials
            } else {
                ctx.status(200); // success
            }

            ctx.json(response);
            System.out.println("Stays in here");

        } catch (Exception e) {
            System.err.println("Database error during login: " + e.getMessage());
            ctx.status(500);
            ctx.json(new LoginResponse(null, null, ErrorMessages.SQLERROR));
        }
    }

}
