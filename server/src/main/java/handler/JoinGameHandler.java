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
        String authToken = ctx.header("Authorization");
        JoinGameRequest joinHandle = (JoinGameRequest) Translation.fromJsonToObject(ctx, JoinGameRequest.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(joinHandle.teamColor(), joinHandle.gameID(), authToken);

        GameService gameService = new GameService(authDAO, gameDAO);
        JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);

        if (joinGameResponse.msg() == null) {
            ctx.status(200);
        } else if (joinGameResponse.msg().equals(ErrorMessages.UNAUTHORIZED)) {
            ctx.status(401);
        } else if (joinGameResponse.msg().equals(ErrorMessages.ALREADYTAKEN)) {
            ctx.status(403);
        } else if (joinGameResponse.msg().equals(ErrorMessages.BADREQUEST)) {
            ctx.status(400);
        } else {
            ctx.status(500);
        }

        ctx.json(joinGameResponse);
    }
}
