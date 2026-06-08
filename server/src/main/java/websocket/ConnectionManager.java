package websocket;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<String, Connection> connections =
            new ConcurrentHashMap<>();

    public void add(String username, Connection connection) {
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public Connection get(String username) {
        return connections.get(username);
    }

    public ConcurrentHashMap<String, Connection> getConnections() {
        return connections;
    }
}
