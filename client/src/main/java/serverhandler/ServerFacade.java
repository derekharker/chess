package serverhandler;

import chess.ChessGame;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.*;
import translatorclient.ClientTranslation;
import ui.ClientMenu;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import java.io.IOException;

public class ServerFacade {
    private final String url;
    private final HTTPCommunicator httpCommunicator = new HTTPCommunicator();
    private ClientMenu client;
    private final WSCommunicator wsCommunicator;


    public ServerFacade(int port, ClientMenu client){
        url = "http://localhost:" + port;
        this.client = client;
        this.wsCommunicator = new WSCommunicator(url, this.client);
    }

    public RegisterResponse register(RegisterRequest request){
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(request);

        try {
            String stringResponse = httpCommunicator.doPost(url + "/user", jsonRequest, null);
            return ClientTranslation.fromJsontoObjectNotRequest(stringResponse, RegisterResponse.class);
        } catch (IOException e) {
            System.out.println("Registering user failed: " + e.getMessage());
            return new RegisterResponse(null, null, e.getMessage());
        }
    }

    public LogoutResponse logout(String authToken) {
        try {
            return new LogoutResponse(httpCommunicator.doDelete(url + "/session", authToken)) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResponse login(LoginRequest request) {
        //translate to json
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(request);

        try {
            String stringResponse = httpCommunicator.doPost(url + "/session", jsonRequest, null);
            LoginResponse testResponse = ClientTranslation.fromJsontoObjectNotRequest(stringResponse, LoginResponse.class);
            return testResponse;
        } catch (IOException e) {
            System.out.println("Registering user failed: " + e.getMessage());
            return new LoginResponse(null, null, "Error logging in");
        }
    }

    public ListGamesResponse listGames(String authToken) {
        try {
            String stringResponse = httpCommunicator.doGet(url + "/game", authToken);
            return ClientTranslation.fromJsontoObjectNotRequest(stringResponse, ListGamesResponse.class);

        } catch (IOException e) {
            System.out.println("Registering user failed: " + e.getMessage());
            return new ListGamesResponse(null, null);
        }
    }

    public JoinGameResponse joinGame(ConnectCommand command, ChessGame.TeamColor teamColor) {
        //translate to json
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(command);
        String httpRequest = (String) ClientTranslation.fromObjectToJson(new JoinGameRequest(teamColor,
                command.getGameID(), command.getAuthToken()));
        //Perform correct HTTP request
        try {
            String stringResponse = httpCommunicator.doPut(url + "/game", httpRequest, command.getAuthToken());
            wsCommunicator.connect(command);
            return ClientTranslation.fromJsontoObjectNotRequest(stringResponse, JoinGameResponse.class);
        } catch (IOException e) {
            return new JoinGameResponse("Join Game Failure");
        }
    }

    public void clearGame(){
        try {
            httpCommunicator.doDelete(url + "/db", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) {
        String jsonRequest = (String) ClientTranslation.fromObjectToJson(request);
        try {
            String response = httpCommunicator.doPost(url + "/game", jsonRequest, authToken);
            return ClientTranslation.fromJsontoObjectNotRequest(response, CreateGameResponse.class);
        } catch (IOException e) {
            System.out.println("Creating Game failed: " + e.getMessage());
        }
        return null;
    }

    public void observeGame(String authToken, int gameID){
        wsCommunicator.connect(new ConnectCommand(authToken, gameID));
    }

    public void leaveGame(LeaveGameCommand command){
        wsCommunicator.leave(command);
    }

    public void makeMoveInGame(MakeMoveCommand command){
        wsCommunicator.makeMove(command);
    }

    public void resignGame(ResignCommand command){
        wsCommunicator.resign(command);
    }
}