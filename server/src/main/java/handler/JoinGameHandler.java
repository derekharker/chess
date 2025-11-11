package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import io.javalin.http.Context;
import request.JoinGameRequest;
import response.JoinGameResponse;
import service.ErrorMessages;
import service.GameService;
import translator.Translation;

public class JoinGameHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            JoinGameRequest joinHandle = (JoinGameRequest) Translation.fromJsonToObject(ctx, JoinGameRequest.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(joinHandle.teamColor(), joinHandle.gameID(), authToken);

            GameService gameService = new GameService(authDAO, gameDAO);
            JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);

            if (joinGameResponse.message() == null) {
                ctx.status(200);
            } else if (joinGameResponse.message().equals(ErrorMessages.BADREQUEST)) {
                ctx.status(400);
            } else if (joinGameResponse.message().equals(ErrorMessages.UNAUTHORIZED)) {
                ctx.status(401);
            } else if (joinGameResponse.message().equals(ErrorMessages.ALREADYTAKEN)) {
                ctx.status(403);
            } else if (joinGameResponse.message().equals(ErrorMessages.SQLERROR)) {
                ctx.status(500);
            }

            ctx.json(joinGameResponse);
        } catch (Exception e) {
            System.err.println("Database error during joinGame: " + e.getMessage());
            ctx.status(500);
            ctx.json(new JoinGameResponse("Error: Database connection failed"));
        }
    }

}
