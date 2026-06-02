package menu;

import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientMenu {

    private final ServerFacade facade;
    private String authToken;
    private String username;
    private boolean loggedIn = false;

    private List<GameData> lastGames = new ArrayList<>();

    public ClientMenu(int port) {
        facade = new ServerFacade(port);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String readPostLoginResponse(String line) {
        try {
            var tokens = line.trim().split("\\s+");

            if (line.isBlank()) {
                return postLoginHelp();
            }

            var cmd = tokens[0].toLowerCase();

            return switch (cmd) {
                case "help" -> postLoginHelp();
                case "create" -> createCommand(tokens);
                case "list" -> list();
                case "join" -> joinCommand(tokens);
                case "observe" -> observeCommand(tokens);
                case "logout" -> logout();
                case "quit" -> quitChess();
                default -> "Unknown command.\n\n" + postLoginHelp();
            };
        } catch (Exception e) {
            return "Something went wrong. Please try again.";
        }
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

    private String createCommand(String[] tokens) {
        if (tokens.length < 2) {
            return "Usage: create <game name>";
        }

        String gameName = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
        return create(gameName);
    }

    private String joinCommand(String[] tokens) {
        if (tokens.length != 3) {
            return "Usage: join <game number> <WHITE|BLACK>";
        }

        return join(tokens[1], tokens[2]);
    }

    private String observeCommand(String[] tokens) {
        if (tokens.length != 2) {
            return "Usage: observe <game number>";
        }

        return observe(tokens[1]);
    }

    private String create(String name) {
        return "Created game: " + name;
    }

    private String list() {
        return "List games is not implemented yet.";
    }

    private String join(String gameNumber, String color) {
        color = color.toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Invalid color. Please enter WHITE or BLACK.";
        }

        return "Joined game " + gameNumber + " as " + color + ".";
    }

    private String observe(String gameNumber) {
        return "Observing game " + gameNumber + ".";
    }

    private String logout() {
        return "Logged out successfully.";
    }

    private String quitChess() {
        return "Bye!";
    }
}