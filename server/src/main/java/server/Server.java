package server;

import handler.*;
import spark.*;
import dataaccess.memory.MemoryGame;
import dataaccess.memory.MemoryAuth;
import dataaccess.memory.MemoryUser;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        MemoryUser userDAO = new MemoryUser();
        MemoryAuth authDAO = new MemoryAuth();
        MemoryGame gameDAO = new MemoryGame();

        //Register endpoints
        Spark.delete("/db", new clearHandler(userDAO, authDAO, gameDAO));
        Spark.post("/user", new RegisterHandler(userDAO, authDAO));
        Spark.delete("/session", new LogoutHandler(userDAO, authDAO));
        Spark.post("/session", new LoginHandler(userDAO, authDAO));
        Spark.post("/game", new CreateGameHandler(authDAO, gameDAO));
        Spark.put("/game", new JoinGameHandler(authDAO, gameDAO));
        Spark.get("game", new ListGamesHandler(authDAO, gameDAO));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();

    }
}
