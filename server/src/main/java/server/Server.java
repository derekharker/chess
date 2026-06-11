package server;

import handler.*;
import io.javalin.*;
import dataaccess.DataAccessException;
import dataaccess.sqldaos.SQLAuthDAO;
import dataaccess.sqldaos.SQLGameDAO;
import dataaccess.sqldaos.SQLUserDAO;
import io.javalin.http.staticfiles.Location;
import websocket.WebSocketHandler;
import java.time.Duration;

import static dataaccess.DatabaseManager.configureDatabase;

public class Server {

    private final Javalin app;

    public Server() {

        app = Javalin.create(config -> {
            config.staticFiles.add("web", Location.CLASSPATH);

            config.jetty.modifyServletContextHandler(handler -> {
                handler.setAttribute(
                        "org.eclipse.jetty.websocket.servlet.WebSocketServletFactory",
                        java.time.Duration.ZERO
                );
            });
        });

        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.out.println("Problem configuring database: " + e.getMessage());
        }

        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();

        WebSocketHandler wsHandler = new WebSocketHandler(authDAO, gameDAO);

        app.ws("/ws", ws -> {
            ws.onMessage(ctx -> wsHandler.onMessage(ctx, ctx.message()));
            ws.onClose(wsHandler::onClose);
        });

        UserHandler userHandler = new UserHandler(userDAO, authDAO);
        SessionHandler sessionHandler = new SessionHandler(userDAO, authDAO);

        GameHandler gameHandler = new GameHandler(authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(gameDAO, authDAO, userDAO);

        app.post("/user", userHandler::register); //join user

        // start the login , logout process
        app.post("/session", sessionHandler::login);
        app.delete("/session", sessionHandler::logout);


        // list game, create and join it
        app.get("/game", gameHandler::listGames);
        app.post("/game", gameHandler::createGame);
        app.put("/game", gameHandler::joinGame);

        app.delete("/db", clearHandler::clear);
    }

    public int run(int port) {
        app.start(port);
        return app.port();
    }

    public void stop() {
        app.stop();
    }

    public static void main(String[] args) {
        new Server().run(8080);
        System.out.println("♕ 240 Chess Server running on port 8080");
    }
}
