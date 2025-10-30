package server;

import handler.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import dataaccess.DataAccessException;
import dataaccess.sqldaos.SQLUserDAO;
import dataaccess.sqldaos.SQLAuthDAO;
import dataaccess.sqldaos.SQLGameDAO;
import static dataaccess.DatabaseManager.configureDatabase;

public class Server {

    private Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        try {
            configureDatabase();
            System.out.println("Database configured successfully.");
        } catch (DataAccessException e) {
            System.out.println("Problem configuring database: " + e.getMessage());
        }

        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();

        javalin.delete("/db", ctx -> new ClearHandler(userDAO, authDAO, gameDAO).handle(ctx));
        javalin.post("/user", ctx -> new RegisterHandler(userDAO, authDAO).handle(ctx));
        javalin.delete("/session", ctx -> new LogoutHandler(userDAO, authDAO).handle(ctx));
        javalin.post("/session", ctx -> new LoginHandler(userDAO, authDAO).handle(ctx));
        javalin.post("/game", ctx -> new CreateGameHandler(authDAO, gameDAO).handle(ctx));
        javalin.put("/game", ctx -> new JoinGameHandler(authDAO, gameDAO).handle(ctx));
        javalin.get("/game", ctx -> new ListGamesHandler(authDAO, gameDAO).handle(ctx));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        int actPort = javalin.port();
        System.out.println("Server running on port " + actPort);
        return actPort;
    }

    public void stop() {
        javalin.stop();
        System.out.println("Server stopped.");
    }
}
