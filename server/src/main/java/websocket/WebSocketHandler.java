package websocket;

import com.google.gson.Gson;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import io.javalin.websocket.WsContext;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.connections = new ConnectionManager();
    }

    public void onMessage(WsContext ctx, String message) {

        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {

            case CONNECT -> connect(ctx, command);
            case MAKE_MOVE -> {
            }
            case LEAVE -> { leave(ctx, command);
            }
            case RESIGN -> {
            }
        }
    }

    public void onClose(WsContext ctx) {
        String username = connections.getUsername(ctx);
        Integer gameID = connections.getGameID(ctx);

        if (username != null && gameID != null) {
            connections.remove(gameID, username);
        }
    }

    private void connect(WsContext ctx, UserGameCommand command) {
        if (!authDAO.isVerifiedAuth(command.getAuthToken())) {
            ctx.send(gson.toJson(new ErrorMessage("Error: invalid auth token")));
            return;
        }

        GameData game = gameDAO.getGame(command.getGameID());
        if (game == null) {
            ctx.send(gson.toJson(new ErrorMessage("Error: game does not exist")));
            return;
        }

        String username = authDAO.getUsernameFromAuth(command.getAuthToken());
        connections.add(command.getGameID(), username, ctx);

        ctx.send(gson.toJson(new LoadGameMessage(game)));

        String message = username + " joined the game";
        for (WsContext other : connections.getOtherConnections(command.getGameID(), username)) {
            other.send(gson.toJson(new NotificationMessage(message)));
        }
    }

    private void leave(WsContext ctx, UserGameCommand command) {

        try {
            if (!authDAO.isVerifiedAuth(command.getAuthToken())) {
                ctx.send(gson.toJson(new ErrorMessage("Error: invalid auth token")));
                return;
            }
            String username = authDAO.getUsernameFromAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());

            if (game == null) {
                ctx.send(gson.toJson(new ErrorMessage("Error: game not found")));
                return;
            }

            if (username.equals(game.getWhiteUsername())) {
                game.setWhiteUsername(null);
            }

            if (username.equals(game.getBlackUsername())) {
                game.setBlackUsername(null);
            }

            gameDAO.updateGame(game);
            connections.remove(command.getGameID(), username);

            NotificationMessage notification = new NotificationMessage(username + " left the game");

            String json = gson.toJson(notification); // json context changer
            for (WsContext other : connections.getOtherConnections(command.getGameID(), username)) {
                other.send(json);
            }

        } catch (Exception ex) {

            ctx.send(gson.toJson(
                    new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
}
