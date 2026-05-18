package server;

import handler.*;
import io.javalin.*;
import dataaccess.memory.MemoryGame;
import dataaccess.memory.MemoryAuth;
import dataaccess.memory.MemoryUser;

public class Server {

    private final Javalin app;

    public Server() {

        app = Javalin.create(config ->
                config.staticFiles.add("web"));

        MemoryUser userDAO = new MemoryUser();
        MemoryAuth authDAO = new MemoryAuth();
        MemoryGame gameDAO = new MemoryGame();

        UserHandler userHandler = new UserHandler(userDAO, authDAO);
        SessionHandler sessionHandler = new SessionHandler(userDAO, authDAO);

        GameHandler gameHandler = new GameHandler(authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(gameDAO, authDAO, userDAO);

        app.post("/user", userHandler::register);

        // start the login , logout process
        app.post("/session", sessionHandler::login);
        app.delete("/session", sessionHandler::logout);

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
