package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import org.eclipse.jetty.server.Authentication;
import request.LoginRequest;
import response.LoginResponse;
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

        UserService loginService = new UserService(authDAO, userDAO);
        LoginResponse sum = loginService.login(loginRequest);

        if (sum.username() == null) {
            ctx.status(401);
        } else {
            ctx.status(200);
        }

        ctx.json(sum);

    }
}
