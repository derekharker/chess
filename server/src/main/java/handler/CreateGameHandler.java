package handler;

import response.CreateGameResponse;
import service.ErrorMessages;
import service.GameService;
import translator.Translation;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;

import io.javalin.http.Context;
import request.CreateGameRequest;
import response.ClearResponse;
import service.SystemService;

public class CreateGameHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void handle(Context ctx) {
        System.out.println("Made it to the handler");

        String authToken = ctx.header("Authorization");
        System.out.println("Auth token entered into createGame success: " + authToken);

        // Parse body
        CreateGameRequest gameName = (CreateGameRequest)
                Translation.fromJsonToObject(ctx, CreateGameRequest.class);

        CreateGameRequest createGameRequest = new CreateGameRequest(gameName.gameName(), authToken);
        GameService gameService = new GameService(authDAO, gameDAO);
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);

        if (createGameResponse.message() == null) {
            ctx.status(200);
        } else if (createGameResponse.message().equals(ErrorMessages.UNAUTHORIZED)) {
            ctx.status(401);
        } else if (createGameResponse.message().equals(ErrorMessages.SQLERROR)) {
            ctx.status(500);
        }

        ctx.json(createGameResponse);
    }
}
