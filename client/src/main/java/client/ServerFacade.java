package client;

import model.AuthData;
import model.GameData;
import ui.ClientException;

import java.util.Collection;

public class ServerFacade {
    private final ClientCommunication communication;

    public ServerFacade(int port) {
        communication = new ClientCommunication(port);
    }

    public AuthData register(String username, String password, String email) throws ClientException {
        var request = new RegisterRequest(username, password, email);
        System.out.println("Registering");
        return communication.post("/user", request, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ClientException {
        var request = new LoginRequest(username, password);
        return communication.post("/session", request, null, AuthData.class);
    }

    public void logout(String authToken) throws ClientException {
        communication.delete("/session", authToken, Void.class);
    }

    public int createGame(String authToken, String gameName) throws ClientException {
        var request = new CreateGameRequest(gameName);
        var response = communication.post("/game", request, authToken, CreateGameResponse.class);
        return response.gameID();
    }

    public Collection<GameData> listGames(String authToken) throws ClientException {
        var response = communication.get("/game", authToken, ListGamesResponse.class);
        return response.games();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws ClientException {
        var request = new JoinGameRequest(playerColor, gameID);
        communication.put("/game", request, authToken, Void.class);
    }

    public void clear() throws ClientException {
        communication.delete("/db", null, Void.class);
    }

    private record RegisterRequest(String username, String password, String email) {
    }

    private record LoginRequest(String username, String password) {
    }

    private record CreateGameRequest(String gameName) {
    }

    private record CreateGameResponse(int gameID) {
    }

    private record ListGamesResponse(Collection<GameData> games) {
    }

    private record JoinGameRequest(String playerColor, int gameID) {
    }
}
