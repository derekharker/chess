package websocket;

import io.javalin.websocket.WsContext;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, WsContext>> games =
            new ConcurrentHashMap<>();

    public void add(int gameID, String username, WsContext ctx) {
        games.computeIfAbsent(gameID, id -> new ConcurrentHashMap<>())
                .put(username, ctx);
    }

    public void remove(int gameID, String username) {
        ConcurrentHashMap<String, WsContext> gameConnections = games.get(gameID);
        if (gameConnections != null) {
            gameConnections.remove(username);
            if (gameConnections.isEmpty()) {
                games.remove(gameID);
            }
        }
    }

    public Collection<WsContext> getGameConnections(int gameID) {
        ConcurrentHashMap<String, WsContext> gameConnections = games.get(gameID);
        return gameConnections == null ? null : gameConnections.values();
    }

    public Collection<WsContext> getOtherConnections(int gameID, String username) {
        ConcurrentHashMap<String, WsContext> gameConnections = games.get(gameID);
        if (gameConnections == null) {
            return java.util.List.of();
        }

        return gameConnections.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(username))
                .map(java.util.Map.Entry::getValue)
                .toList();
    }
}
