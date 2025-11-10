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

        try {
            CreateGameRequest body = (CreateGameRequest) Translation.fromJsonToObject(ctx, CreateGameRequest.class);
            if (body == null || body.gameName() == null) {
                ctx.status(400);
                ctx.json(new CreateGameResponse(null,"Error: Invalid request"));
                return;
            }

            CreateGameRequest createGameRequest = new CreateGameRequest(body.gameName(), authToken);
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
        } catch (Exception e) {
            System.err.println("Database error during createGame: " + e.getMessage());
            ctx.status(500);
            ctx.json(new CreateGameResponse(null, "Error: Database connection failed"));
        }
    }
}
