package menu;

import client.ServerFacade;
import client.WebSocketFacade;
import model.AuthData;
import model.GameData;
import ui.ClientException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClientMenu {

    private final ServerFacade facade;
    private String authToken;
    private String username;
    private boolean loggedIn = false;
    private final int port;

    private List<GameData> lastGames = new ArrayList<>();
    private final BoardPrinter boardPrinter = new BoardPrinter();

    private WebSocketFacade webSocket;
    private Integer currentGameID;

    private boolean inGame = false;
    private String playerColor;
    private GameData currentGame;

    public ClientMenu(int port) {
        this.port = port;
        facade = new ServerFacade(port);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String eval(String line) {
        if (line == null || line.isBlank()) {
            return loggedIn ? postLoginHelp() : preLoginHelp();
        }

        try {
            if (inGame) {
                return evalGameplay(line);
            }
            else if (loggedIn) {
                return evalPostLogin(line);
            } else {
                return evalPreLogin(line);
            }
        } catch (ClientException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Something went wrong. Please try again.";
        }
    }

    private String evalGameplay(String line) throws ClientException {
        return "here";
    }

    private String evalPreLogin(String line) throws ClientException {
        var tokens = line.trim().split("\\s+");
        var command = tokens[0].toLowerCase();

        return switch (command) {
            case "help" -> preLoginHelp();
            case "quit" -> "quit";
            case "login" -> login(tokens);
            case "register" -> register(tokens);
            default -> "Unknown command.\n\n" + preLoginHelp();
        };
    }

    private String evalPostLogin(String line) throws ClientException {
        var tokens = line.trim().split("\\s+");
        var command = tokens[0].toLowerCase();

        return switch (command) {
            case "help" -> postLoginHelp();
            case "create" -> createCommand(tokens);
            case "list" -> list();
            case "join" -> joinCommand(tokens);
            case "observe" -> observeCommand(tokens);
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> "Unknown command.\n\n" + postLoginHelp();
        };
    }

    private String preLoginHelp() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - create an account
                login <USERNAME> <PASSWORD> - log in
                quit - quit chess
                help - show possible commands
                """;
    }

    private String postLoginHelp() {
        return """
                create <NAME> - create a new game
                list - list all games
                join <GAME_NUMBER> <WHITE|BLACK> - join a game as white or black
                observe <GAME_NUMBER> - observe a game
                logout - log out
                quit - quit chess
                help - show possible commands
                """;
    }

    private String register(String[] tokens) throws ClientException {
        if (tokens.length != 4) {
            return "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }

        AuthData authData = facade.register(tokens[1], tokens[2], tokens[3]);

        authToken = authData.authToken();
        username = authData.username();
        loggedIn = true;

        return "Registered and logged in as " + username + ".";
    }

    private String login(String[] tokens) throws ClientException {
        if (tokens.length != 3) {
            return "Usage: login <USERNAME> <PASSWORD>";
        }

        AuthData authData = facade.login(tokens[1], tokens[2]);

        authToken = authData.authToken();
        username = authData.username();
        loggedIn = true;

        return "Logged in as " + username + ".";
    }

    private String logout() throws ClientException {
        facade.logout(authToken);

        authToken = null;
        username = null;
        loggedIn = false;
        lastGames.clear();
        return "Logged out successfully.";
    }

    //cmd to create
    private String createCommand(String[] tokens) throws ClientException {
        if (tokens.length < 2) {
            return "Usage: create <game name>";
        }

        String gameName = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
        int gameID = facade.createGame(authToken, gameName);

        return "Created game: " + gameName;
    }

    private String list() throws ClientException {
        Collection<GameData> games = facade.listGames(authToken);
        lastGames = new ArrayList<>(games);

        if (lastGames.isEmpty()) {
            return "No games have been created yet.";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lastGames.size(); i++) {
            GameData game = lastGames.get(i);

            result.append(i + 1)
                    .append(". ")
                    .append(game.getGameName())
                    .append(" | White: ")
                    .append(displayPlayer(game.getWhiteUsername()))
                    .append(" | Black: ")
                    .append(displayPlayer(game.getBlackUsername()))
                    .append("\n");
        }

        return result.toString();
    }

    private String joinCommand(String[] tokens) throws ClientException {
        if (tokens.length != 3) {
            return "Usage: join <game number> <WHITE|BLACK>";
        }

        int listNumber = parseGameNumber(tokens[1]);
        String color = tokens[2].toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Invalid color. Please enter WHITE or BLACK.";
        }

        GameData game = getGameFromListNumber(listNumber);
        //now with actual joining of games
        facade.joinGame(authToken, game.getGameID(), color);


        currentGameID = game.getGameID();
        currentGame = game;
        playerColor = color;
        inGame = true;

        webSocket = new WebSocketFacade("http://localhost:" + port, this::handleServerMessage);
        webSocket.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, currentGameID));

        String board = color.equals("BLACK")
                ? boardPrinter.drawBlackBoard()
                : boardPrinter.drawWhiteBoard();

        return "Joined " + game.getGameName() + " as " + color + "\n\n" + board;
    }

    private String observeCommand(String[] tokens) throws ClientException {
        if (tokens.length != 2) {
            return "Usage: observe <game number>";
        }
        int listNumber = parseGameNumber(tokens[1]);
        GameData game = getGameFromListNumber(listNumber);
        //return string for user
        currentGameID = game.getGameID();
        currentGame = game;
        playerColor = "OBSERVER";
        inGame = true;

        webSocket = new WebSocketFacade("http://localhost:" + port, this::handleServerMessage);
        webSocket.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, currentGameID));
//same as joincommand
        return "Observing " + game.getGameName();
    }

    private int parseGameNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Game number must be a number.");
        }
    }

    private GameData getGameFromListNumber(int listNumber) {
        if (lastGames.isEmpty()) {
            throw new IllegalArgumentException("Use list before choosing a game.");
        }
        if (listNumber < 1 || listNumber > lastGames.size()) {
            throw new IllegalArgumentException("That game number is not in the list.");
        }
        return lastGames.get(listNumber - 1);
    }

    private String displayPlayer(String player) {
        if (player == null || player.isBlank()) {
            return "empty";
        }

        return player;
    }

    private void handleServerMessage(ServerMessage message) {
        System.out.println("Received: " + message.getServerMessageType());
    }
}