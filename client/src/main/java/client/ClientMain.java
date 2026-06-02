package client;

import menu.ClientMenu;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var menu = new ClientMenu(8080);

        System.out.println("Welcome to Chess!");
        System.out.println("Type help to get started.");

        while (true) {
            System.out.print(">>> ");
            String line = scanner.nextLine();

            String result = menu.eval(line);

            if (result.equals("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            System.out.println(result);
        }
    }
}