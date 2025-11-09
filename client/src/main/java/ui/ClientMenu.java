package ui;

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

        }
    }
}
