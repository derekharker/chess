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

        if (loginRequest == null || loginRequest.username() == null || loginRequest.password() == null) {
            ctx.status(400);
            ctx.json(new LoginResponse(null, null, "Error: Invalid request"));
            return;
        }

        UserService loginService = new UserService(userDAO, authDAO);
        try {
            LoginResponse response = loginService.login(loginRequest);

            if (response.username() == null) {
                ctx.status(401); // bad credentials
            } else {
                ctx.status(200); // success
            }

            ctx.json(response);

        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new LoginResponse(null, null, "Error"));
        }
    }

}
