package handler;

import com.google.gson.Gson;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;

import io.javalin.http.Context;

import request.CreateGameRequest;
import request.JoinGameRequest;

import response.CreateGameResponse;
import response.JoinGameResponse;
import response.ListGamesResponse;

import service.ErrorMessages;
import service.GameService;

import java.util.Map;

public class GameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        gameService = new GameService(authDAO, gameDAO);
    }

    public void listGames(Context ctx) {
        try {
            String auth = ctx.header("authorization");
            ListGamesResponse response = gameService.listGames(auth);

            if (response.message() != null) {
                ctx.status(401);
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

    public void createGame(Context ctx) {
        try {
            String auth = ctx.header("authorization");

            CreateGameRequest request =
                    gson.fromJson(ctx.body(), CreateGameRequest.class);

            CreateGameRequest updated =
                    new CreateGameRequest(request.gameName(), auth);

            CreateGameResponse response =
                    gameService.createGame(updated);

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
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", ErrorMessages.SQLERROR)));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String auth = ctx.header("authorization");

            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameRequest updated =
                    new JoinGameRequest(
                            request.playerColor(),
                            request.gameID(),
                            auth
                    );

            JoinGameResponse response =
                    gameService.joinGame(updated);

            if (response.message() != null) {
                if (response.message().contains("already taken")) {
                    ctx.status(403);
                } else if (response.message().contains("unauthorized")) {
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
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", ErrorMessages.SQLERROR)));
        }
    }
}