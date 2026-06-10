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
        return gameConnections == null ? java.util.List.of() : gameConnections.values();
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

    //finding ctx of game and returning username heree
    public String getUsername(WsContext ctx) {

        for (var game : games.values()) {
            for (var entry : game.entrySet()) {
                if (entry.getValue().equals(ctx)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    public Integer getGameID(WsContext ctx) {

        for (var entry : games.entrySet()) {
            for (var session : entry.getValue().values()) {
                if (session.equals(ctx)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }
}
