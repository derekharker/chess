package server;

import handler.*;
import io.javalin.Javalin;
import dataaccess.memory.MemoryGame;
import dataaccess.memory.MemoryAuth;
import dataaccess.memory.MemoryUser;

public class Server {

    private Javalin app;

    public int run(int desiredPort) {

        MemoryUser userDAO = new MemoryUser();
        MemoryAuth authDAO = new MemoryAuth();
        MemoryGame gameDAO = new MemoryGame();

        app = Javalin.create(config -> {
            config.staticFiles.add("/web");
        }).start(desiredPort);

        // Register endpoints
        app.delete("/db", ctx -> new ClearHandler(userDAO, authDAO, gameDAO).handle(ctx));
        app.post("/user", ctx -> new RegisterHandler(userDAO, authDAO).handle(ctx));
        app.delete("/session", ctx -> new LogoutHandler(userDAO, authDAO).handle(ctx));
        app.post("/session", ctx -> new LoginHandler(userDAO, authDAO).handle(ctx));
        app.post("/game", ctx -> new CreateGameHandler(authDAO, gameDAO).handle(ctx));
        app.put("/game", ctx -> new JoinGameHandler(authDAO, gameDAO).handle(ctx));
        app.get("/game", ctx -> new ListGamesHandler(authDAO, gameDAO).handle(ctx));

        return desiredPort;
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
