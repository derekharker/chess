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

            int status;

            if (response.message() == null) {
                status = 200;
            } else {
                status = 401;
            }

            sendJson(ctx, status, response);

        } catch (Exception e) {
            sendServerError(ctx);
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

            int status = getCreateGameStatus(response);
            sendJson(ctx, status, response);

        } catch (Exception e) {
            sendServerError(ctx);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String auth = ctx.header("authorization");

            JoinGameRequest request =
                    gson.fromJson(ctx.body(), JoinGameRequest.class);

            JoinGameRequest updated =
                    new JoinGameRequest(
                            request.playerColor(),
                            request.gameID(),
                            auth
                    );

            JoinGameResponse response =
                    gameService.joinGame(updated);

            int status = getJoinGameStatus(response);
            sendJson(ctx, status, response);

        } catch (Exception e) {
            sendServerError(ctx);
        }
    }

    //needed to add helper functions so code duplication quality isn't flagged
    private void sendJson(Context ctx, int status, Object response) {
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(response));
    }

    //same thing here
    private void sendServerError(Context ctx) {
        sendJson(ctx, 500, Map.of("message", ErrorMessages.SQLERROR));
    }

    //and same thing here too
    private int getCreateGameStatus(CreateGameResponse response) {
        if (response.message() == null) {
            return 200;
        }

        if (response.message().contains("unauthorized")) {
            return 401;
        }

        return 400;
    }

    private int getJoinGameStatus(JoinGameResponse response) {
        if (response.message() == null) {
            return 200;}
        if (response.message().contains("already taken")) {
            return 403;}
        if (response.message().contains("unauthorized")) {
            return 401;}
        return 400;
    }
}