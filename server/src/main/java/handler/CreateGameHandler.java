package handler;

import response.CreateGameResponse;
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
        String authToken = ctx.header("Authorization");
        CreateGameRequest gameName = (CreateGameRequest) Translation.fromJsonToObject(ctx, CreateGameRequest.class);

        CreateGameRequest createGameRequest = new CreateGameRequest(gameName.gameName(), authToken);

        //Call service layer
        GameService gameService = new GameService(authDAO, gameDAO);
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);

        if (createGameResponse.message() != null) {
            // Switch case same as Login Handler
            switch (createGameResponse.message()) {
                case "Error: bad request" -> ctx.status(400);
                case "Error: unauthorized" -> ctx.status(401);

                default -> ctx.status(500);
            }
        } else {
            ctx.status(200);
        }

        ctx.json(createGameResponse);
    }
}
