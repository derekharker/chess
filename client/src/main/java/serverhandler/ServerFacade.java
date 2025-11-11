package serverhandler;

import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.*;
import translatorclient.ClientTranslation;

import java.io.IOException;

public class ServerFacade {
    private final String url;
    private final ClientCommunication clientCommunicator = new ClientCommunication();


    public ServerFacade(int port){
        url = "http://localhost:" + port;
    }

    public RegisterResponse register(RegisterRequest request){
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(request);

        try {
            String stringResponse = clientCommunicator.doPost(url + "/user", jsonRequest, null);
            return ClientTranslation.fromJsontoObjectNotRequest(stringResponse, RegisterResponse.class);
        } catch (IOException e) {
            System.out.println("Registering user failed: " + e.getMessage());
            return new RegisterResponse(null, null, e.getMessage());
        }
    }

    public LogoutResponse logout(String authToken) {
        try {
            return new LogoutResponse(clientCommunicator.doDelete(url + "/session", authToken)) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResponse login(LoginRequest request) {
        //translate to json
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(request);

        try {
            String stringResponse = clientCommunicator.doPost(url + "/session", jsonRequest, null);
            LoginResponse testResponse = ClientTranslation.fromJsontoObjectNotRequest(stringResponse, LoginResponse.class);
            return testResponse;
        } catch (IOException e) {
            System.out.println("Registering user failed: " + e.getMessage());
            return new LoginResponse(null, null, "Error logging in");
        }
    }

    public ListGamesResponse listGames(String authToken) {
        try {
            String stringResponse = clientCommunicator.doGet(url + "/game", authToken);
            return ClientTranslation.fromJsontoObjectNotRequest(stringResponse, ListGamesResponse.class);

        } catch (IOException e) {
            System.out.println("Registering user failed: " + e.getMessage());
            return new ListGamesResponse(null, null);
        }
    }

    //Join the game with valid request
    public JoinGameResponse joinGame(JoinGameRequest request) {
        try {
            var obj = new com.google.gson.JsonObject();
            if (request.teamColor() != null) {
                obj.addProperty("playerColor", request.teamColor().name()); // "WHITE"/"BLACK"
            }
            obj.addProperty("gameID", request.gameID()); // numeric, NOT quoted
            String body = obj.toString();

            String resp = clientCommunicator.doPut(url + "/game", body, request.authToken());

            // Join often returns 204 No Content on success; treat empty body as success
            if (resp == null || resp.isBlank()) {
                return new JoinGameResponse(null);
            }
            return ClientTranslation.fromJsontoObjectNotRequest(resp, JoinGameResponse.class);
        } catch (IOException e) {
            return new JoinGameResponse("Join Game Failure");
        }
    }

    public void clearGame(){
        try {
            clientCommunicator.doDelete(url + "/db", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) {
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(request);
        try {
            String response = clientCommunicator.doPost(url + "/game", jsonRequest, authToken);
            return ClientTranslation.fromJsontoObjectNotRequest(response, CreateGameResponse.class);
        } catch (IOException e) {
            System.out.println("Creating Game failed: " + e.getMessage());
        }
        return null;
    }
}