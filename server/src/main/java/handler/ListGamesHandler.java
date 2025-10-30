package handler;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import io.javalin.http.Context;
import response.ListGamesResponse;
import service.ErrorMessages;
import service.GameService;

public class ListGamesHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGamesHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            GameService listGames = new GameService(authDAO, gameDAO);
            ListGamesResponse listGamesResponse = listGames.listGames(authToken);

            if (listGamesResponse.message() == null) {
                ctx.status(200);
            } else if (listGamesResponse.message().equals(ErrorMessages.UNAUTHORIZED)) {
                ctx.status(401);
            } else if (listGamesResponse.message().equals(ErrorMessages.SQLERROR)) {
                ctx.status(500);
            }

            ctx.json(listGamesResponse);
        } catch (Exception e) {
            System.err.println("Database error during listGames: " + e.getMessage());
            ctx.status(500);
            ctx.json(new ListGamesResponse(null, "Error: Database connection failed"));
        }
    }

}
