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

        if (createGameResponse.msg() == null) {
            ctx.status(200);
        } else {
            ctx.status(401);
        }

        ctx.json(createGameResponse);
    }
}
