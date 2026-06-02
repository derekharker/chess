package server;

import handler.*;
import io.javalin.*;
import dataaccess.DataAccessException;
import dataaccess.sqldaos.SQLAuthDAO;
import dataaccess.sqldaos.SQLGameDAO;
import dataaccess.sqldaos.SQLUserDAO;

import static dataaccess.DatabaseManager.configureDatabase;

public class ServerMain {

    private final Javalin app;

    public static void main(String[] args) {
        new ServerMain().run(8080);
        System.out.println("♕ 240 Chess Server running on port 8080");
    }

    public ServerMain() {

        app = Javalin.create(config -> config.staticFiles.add("web"));

        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.out.println("Problem configuring database: " + e.getMessage());
        }

        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();

        UserHandler userHandler = new UserHandler(userDAO, authDAO);
        SessionHandler sessionHandler = new SessionHandler(userDAO, authDAO);

        GameHandler gameHandler = new GameHandler(authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(gameDAO, authDAO, userDAO);

        app.post("/user", userHandler::register);

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
}
