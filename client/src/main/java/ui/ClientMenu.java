package ui;

import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import java.util.HashMap;
import java.util.Scanner;

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
                System.out.print(out);
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

    private String preLoginHelp() {
        return "Enter 1 to see help options" + "\n" +
                "Enter 2 to quit" + "\n" +
                "Enter 3 to login" + "\n" +
                "Enter 4 to register" + "\n";
    }

    public void postLoginUI(String authToken) {
        System.out.print("Login success, select an option below: ");

        String login = "Logged in";
        while (!login.equals("Logged out")) {
            printPostLogin();

            Scanner scanner = new Scanner(System.in);

            String ln = scanner.nextLine();
            try {
                login = evalPostLogin(ln, authToken);
                System.out.print(login);
            } catch (Throwable ex) {
                var msg = ex.toString();
                System.out.print(msg);
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


}
