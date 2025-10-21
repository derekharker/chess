package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import io.javalin.http.Context;
import request.RegisterRequest;
import response.RegisterResponse;
import service.ErrorMessages;
import service.UserService;
import translator.Translation;

public class RegisterHandler {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void handle(Context ctx) {
        RegisterRequest registerRequest = (RegisterRequest) Translation.fromJsonToObject(ctx, RegisterRequest.class);

        UserService registerService = new UserService(authDAO, userDAO);
        RegisterResponse result = registerService.register(registerRequest);

        if (result.msg() == null) {
            ctx.status(200);
        } else {
            if (result.msg().equals(ErrorMessages.ALREADYTAKEN)) {
                ctx.status(403);
            } else if (result.msg().equals(ErrorMessages.BADREQUEST)) {
                ctx.status(400);
            }
        }

        ctx.json(result);
    }
}
