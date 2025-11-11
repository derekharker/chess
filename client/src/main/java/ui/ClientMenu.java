package ui;

import chess.ChessGame;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.ListGamesResponse;
import response.LoginResponse;
import response.RegisterResponse;

import java.util.HashMap;
import java.util.Scanner;
import serverhandler.ServerFacade;

public class ClientMenu {
    private final ServerFacade facade;
    HashMap<Integer, Integer> gameIDMap;

    public ClientMenu(int port) {
        facade = new ServerFacade(port);
        gameIDMap = new HashMap<>();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var out = "";

        while (!out.equals("quit")) {
            System.out.println("Welcome to Chess! Select an option below: ");
            printPreLogin();
            String ln = scanner.nextLine();

            try {
                out = evalPreLogin(ln);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }

        System.out.println();
    }

    private void printPreLogin() {
        System.out.println("1: Help");
        System.out.println("2: Quit");
        System.out.println("3: Login");
        System.out.println("4: Register");
    }

    private String listGames(String authToken) {
        ListGamesResponse listGamesResponse =  facade.listGames(authToken);

        int gameNumber = 1;
        for (GameData game : listGamesResponse.games()){
            System.out.println("Game Number: " + gameNumber + ", Game Name: " + game.getGameName() +
                    ", White Username: " + game.getWhiteUsername() + ", Black Username: " +
                    game.getBlackUsername());
            gameIDMap.put(gameNumber, game.getGameID());
            gameNumber ++;
        }
        return "";
    }

    private void showGame(int gameID, String authToken){
        BoardCreation board = new BoardCreation();
        ListGamesResponse listGamesResponse = facade.listGames(authToken);
        for (GameData game : listGamesResponse.games()){
            if (gameIDMap.get(gameID) == game.getGameID()){
                System.out.println("White Orientation");
                board.createBoard(ChessGame.TeamColor.WHITE, game.getGame().getBoard());
                System.out.println("Black Orientation");
                board.createBoard(ChessGame.TeamColor.BLACK, game.getGame().getBoard());
            }
        }
    }

    public String evalPreLogin(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";

            return switch (cmd) {
                case "2" -> "quit";
                case "3" -> login();
                case "4" -> register();
                default -> preLoginHelp();
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String register() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        RegisterResponse reg = facade.register(new RegisterRequest(username, password, email));
        if (reg.authToken() != null) {
            postLoginUI(reg.authToken());
            return "";
        } else {
            return reg.message();
        }
    }

    private String login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        LoginResponse resp = facade.login(new LoginRequest(username, password));
        if (resp.authToken() != null) {
            postLoginUI(resp.authToken());
            return "";
        } else {
            return resp.message();
        }
    }

    private String observeGame(String authToken) {
        listGames(authToken);
        Scanner scanner = new Scanner(System.in);

        int gameNumber = -1;
        while (true) {
            System.out.print("Enter the game number you'd like to observe: ");
            String input = scanner.nextLine();
            try {
                gameNumber = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        try {
            showGame(gameNumber, authToken);
        } catch (Exception e) {
            System.err.println("Game does not exist");
        }

        return "";
    }


    private String preLoginHelp() {
        return "Enter 1 to see help options" + "\n" +
                "Enter 2 to quit" + "\n" +
                "Enter 3 to login" + "\n" +
                "Enter 4 to register" + "\n";
    }

    public void postLoginUI(String authToken) {
        System.out.println("Login success, select an option below: ");

        String login = "Logged in";
        while (!login.equals("Logged out")) {
            printPostLogin();

            Scanner scanner = new Scanner(System.in);

            String ln = scanner.nextLine();
            try {
                login = evalPostLogin(ln, authToken);
                System.out.println(login);
            } catch (Throwable ex) {
                var msg = ex.toString();
                System.out.println("Error is here");
            }
        }
        System.out.println();
    }

    private void printPostLogin() {
        System.out.println("1: Help");
        System.out.println("2: Logout");
        System.out.println("3: Create Game");
        System.out.println("4: List Games");
        System.out.println("5: Play Game");
        System.out.println("6: Observe Game");
    }

    public String evalPostLogin(String line, String authToken) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";

            return switch (cmd) {
                case "2" -> logout(authToken);
                case "3" -> createGame(authToken);
                case "4" -> listGames(authToken);
                case "5" -> playGame(authToken);
                case "6" -> observeGame(authToken);
                default -> postLoginHelp();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String postLoginHelp(){
        return "Enter 1 to see help options" + "\n" +
                "Enter 2 to Logout" + "\n" +
                "Enter 3 to Create a Game" + "\n" +
                "Enter 4 to List the Games" + "\n" +
                "Enter 5 to Play a Game" + "\n" +
                "Enter 6 to Observe a Game" + "\n";
    }

    private String logout(String authToken) {
        facade.logout(authToken);

        return "Logged out";
    }

    private String playGame(String authToken) {
        listGames(authToken);
        Scanner scanner = new Scanner(System.in);

        int num = -1;
        while (true) {
            System.out.print("Enter which game to join: ");
            String input = scanner.nextLine();
            try {
                num = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        ChessGame.TeamColor teamColor = ChessGame.TeamColor.WHITE; // default
        System.out.print("Enter which color you want to play. w for white and b for black (white is default): ");
        String color = scanner.nextLine().trim().toLowerCase();

        if (color.equals("b")) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else if (!color.equals("w") && !color.isEmpty()) {
            System.out.println("Invalid color choice. Defaulting to white.");
        }

        try {
            facade.joinGame(new JoinGameRequest(teamColor, gameIDMap.get(num), authToken));
            showGame(num, authToken);
        } catch (Exception e) {
            System.err.println("Game does not exist");
        }

        return "";
    }


    private String createGame(String authToken) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter new game name: ");
        String gameName = scanner.nextLine();

        facade.createGame(new CreateGameRequest(gameName, null), authToken);


        return "";
    }


}
