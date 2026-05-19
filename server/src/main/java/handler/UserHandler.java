package handler;

import com.google.gson.Gson;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;

import io.javalin.http.Context;

import request.RegisterRequest;

import response.RegisterResponse;
import service.ErrorMessages;
import service.UserService;
import java.util.Map;
public class UserHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        userService = new UserService(userDAO, authDAO);
    }

    public void register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResponse response = userService.register(request);
            if (response.message() != null) {
                if (response.message().contains("already taken")) {
                    ctx.status(403);
                } else {
                    ctx.status(400);
                }
            } else {
                ctx.status(200);
            }

            ctx.contentType("application/json");
            ctx.result(gson.toJson(response));
        } catch (Exception e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", ErrorMessages.SQLERROR)));
        }
    }
}