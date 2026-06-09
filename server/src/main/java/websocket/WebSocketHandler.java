package websocket;

import com.google.gson.Gson;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import io.javalin.websocket.WsContext;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;

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
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
    }

    private void connect(WsContext ctx, UserGameCommand command) {
        try {
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

            connections.add(username, new Connection(username, command.getGameID(), ctx));
            ctx.send(gson.toJson(new LoadGameMessage(game)));

        } //exceptions for auth token error
        catch (Exception ex) {
            ctx.send(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
}
