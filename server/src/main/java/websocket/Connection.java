package websocket;

import io.javalin.websocket.WsContext;

public class Connection {

    private final String username;
    private final int gameID;
    private final WsContext session;

    public Connection(String username, int gameID, WsContext session) {
        this.username = username;
        this.gameID = gameID;
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public int getGameID() {
        return gameID;
    }

    public WsContext getSession() {
        return session;
    }
}
