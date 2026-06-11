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
import websocket.commands.MakeMoveCommand;
import chess.*;
import websocket.messages.ServerMessage;

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
            case MAKE_MOVE -> { makeMove(ctx, gson.fromJson(message, MakeMoveCommand.class));
            }
            case LEAVE -> { leave(ctx, command);
            }
            case RESIGN -> { resign(ctx, command);
            }
            case PING -> {}
        }
    }

    public void onClose(WsContext ctx) {
        connections.remove(ctx);
    }

    private void makeMove(WsContext ctx, MakeMoveCommand command) {

        try {
            if (!authDAO.isVerifiedAuth(command.getAuthToken())) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }
            String username = authDAO.getUsernameFromAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());

            if (gameData == null) {
                sendError(ctx, "Error: game not found");
                return;
            }

            ChessGame game = gameData.getGame();
            ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);

            if (playerColor == null) {
                sendError(ctx, "Error: observers cannot move");
                return;
            }
            if (game.isGameOver()) {
                sendError(ctx, "Error: game is over");
                return;
            }
            if (game.getTeamTurn() != playerColor) {
                sendError(ctx, "Error: not your turn");
                return;
            }

            ChessMove move = command.getMove();
            game.makeMove(move);
            gameDAO.updateGame(gameData);

            sendToGame(command.getGameID(), new LoadGameMessage(gameData));
            sendToOthers(command.getGameID(), ctx, new NotificationMessage(username + " made a move"));

            sendGameStatusMessages(command.getGameID(), game, playerColor);

        } catch (Exception ex) {
            sendError(ctx, "Error: " + ex.getMessage());
        }
    }

    private ChessGame.TeamColor getPlayerColor(String username, GameData gameData) {
        if (username.equals(gameData.getWhiteUsername())) {
            return ChessGame.TeamColor.WHITE;}
        if (username.equals(gameData.getBlackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    private void sendError(WsContext ctx, String message) {
        ctx.send(gson.toJson(new ErrorMessage(message)));
    }

    private void sendToGame(int gameID, ServerMessage message) {
        String json = gson.toJson(message);

        for (Connection connection : connections.getGameConnections(gameID)) {
            connection.getSession().send(json);
        }
    }

    private void sendGameStatusMessages(int gameID, ChessGame game, ChessGame.TeamColor playerColor) {
        ChessGame.TeamColor opponent = playerColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (game.isInCheckmate(opponent)) {
            game.setGameOver(true);
            sendToGame(gameID, new NotificationMessage(opponent + " is in checkmate"));
        } else if (game.isInStalemate(opponent)) {
            game.setGameOver(true);
            sendToGame(gameID, new NotificationMessage(opponent + " is in stalemate"));
        } else if (game.isInCheck(opponent)) {
            sendToGame(gameID, new NotificationMessage(opponent + " is in check"));
        }
    }

    private void sendToOthers(int gameID, WsContext ctx, ServerMessage message) {
        String json = gson.toJson(message);

        for (Connection connection : connections.getOtherConnections(gameID, ctx)) {
            connection.getSession().send(json);
        }
    }

    private void resign(WsContext ctx, UserGameCommand command) {

        try {

            if (!authDAO.isVerifiedAuth(command.getAuthToken())) {
                sendError(ctx, "Error: invalid auth token");
                return;
            }

            String username = authDAO.getUsernameFromAuth(command.getAuthToken());
            GameData gameData = gameDAO.getGame(command.getGameID());

            if (gameData == null) {
                sendError(ctx, "Error: game not found");
                return;
            }

            if (!username.equals(gameData.getWhiteUsername()) &&
                    !username.equals(gameData.getBlackUsername())) {
                sendError(ctx, "Error: only players can resign");
                return;
            }

            if (gameData.getGame().isGameOver()) {
                sendError(ctx, "Error: game is already over");
                return;
            }

            gameData.getGame().setGameOver(true);
            gameDAO.updateGame(gameData);

            sendToGame(command.getGameID(), new NotificationMessage(username + " resigned"));

        } catch (Exception ex) {
            sendError(ctx, "Error: " + ex.getMessage());
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

        String message; //more specific notification to all players

        if (username.equals(game.getWhiteUsername())) {
            message = username + " connected as WHITE";
        }
        else if (username.equals(game.getBlackUsername())) {
            message = username + " connected as BLACK";
        }
        else {
            message = username + " connected as an observer";
        }

        sendToOthers(command.getGameID(), ctx, new NotificationMessage(message));
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
            connections.remove(ctx);

            NotificationMessage notification = new NotificationMessage(username + " left the game");
            sendToGame(command.getGameID(), notification);

        } catch (Exception ex) {

            ctx.send(gson.toJson(
                    new ErrorMessage("Error: " + ex.getMessage())));
        }
    }
}
