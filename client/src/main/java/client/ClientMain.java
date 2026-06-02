package client;

import menu.ClientMenu;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var menu = new ClientMenu();

        System.out.println("♕ Welcome to Chess!");
        System.out.println("Type help to get started.");

        while (true) {
            System.out.print(">>> ");
            String line = scanner.nextLine();

            String result = menu.readPostLoginResponse(line);
            System.out.println(result);

            if (line.trim().equalsIgnoreCase("quit")) {
                break;
            }
        }
    }
}