package handler;

import com.google.gson.Gson;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;

import io.javalin.http.Context;

import request.LoginRequest;

import response.LoginResponse;
import response.LogoutResponse;

import service.ErrorMessages;
import service.UserService;

import java.util.Map;

public class SessionHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public SessionHandler(UserDAO userDAO, AuthDAO authDAO) {
        userService = new UserService(userDAO, authDAO);
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResponse response = userService.login(request);

            if (response.message() != null) {
                if (response.message().contains("unauthorized")) {
                    ctx.status(401);
                } else {
                    ctx.status(400);
                }
            } else {
                ctx.status(200);
            }

            ctx.contentType("application/json");
            ctx.result(gson.toJson(response));

        } catch (Exception e) {
            sqlError(ctx);
        }
    }

    public void logout(Context ctx) {
        try {
            String auth = ctx.header("authorization");
            LogoutResponse response = userService.logout(auth);

            if (response.message() != null) {
                ctx.status(401);
            } else {
                ctx.status(200);
            }

            ctx.contentType("application/json");
            ctx.result(gson.toJson(response));

        } catch (Exception e) {
            sqlError(ctx);
        }
    }

    private void sqlError(Context ctx) {
        ctx.status(500);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", ErrorMessages.SQLERROR)));
    }
}