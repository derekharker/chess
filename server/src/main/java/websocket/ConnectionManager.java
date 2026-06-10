package websocket;

import io.javalin.websocket.WsContext;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, Collection<Connection>> games =
            new ConcurrentHashMap<>();

    public void add(int gameID, String username, WsContext ctx) {
        Connection connection = new Connection(username, gameID, ctx);

        games.computeIfAbsent(gameID, id -> new CopyOnWriteArrayList<>())
                .add(connection);
    }

    public void remove(WsContext ctx) {
        for (var entry : games.entrySet()) {
            Collection<Connection> gameConnections = entry.getValue();

            gameConnections.removeIf(connection ->
                    connection.getSession().equals(ctx));

            if (gameConnections.isEmpty()) {
                games.remove(entry.getKey());
            }
        }
    }

    public Collection<Connection> getGameConnections(int gameID) {
        Collection<Connection> gameConnections = games.get(gameID);
        return gameConnections == null ? java.util.List.of() : gameConnections;
    }

    public Collection<Connection> getOtherConnections(int gameID, WsContext ctx) {
        Collection<Connection> gameConnections = games.get(gameID);

        if (gameConnections == null) {
            return java.util.List.of();
        }

        return gameConnections.stream()
                .filter(connection -> !connection.getSession().equals(ctx))
                .toList();
    }

    public String getUsername(WsContext ctx) {
        for (var game : games.values()) {
            for (var connection : game) {
                if (connection.getSession().equals(ctx)) {
                    return connection.getUsername();
                }
            }
        }

        return null;
    }

    public Integer getGameID(WsContext ctx) {
        for (var entry : games.entrySet()) {
            for (var connection : entry.getValue()) {
                if (connection.getSession().equals(ctx)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }
}