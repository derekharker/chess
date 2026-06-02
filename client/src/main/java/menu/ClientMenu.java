package menu;

public class ClientMenu {
    private void postLoginInfo() {
        System.out.println("create <NAME> - a game");
        System.out.println("list - games");
        System.out.println("join <ID> [WHITE|BLACK] - a game");
        System.out.println("observe <ID> - a game");
        System.out.println("logout - when done");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

    public String readPostLoginResponse(String line) {
        try {
            var tokens = line.toLowerCase().split("");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";

            return switch (cmd) {
                case "create" -> create(tokens[1]);
                case "list" -> list();
                case "join" -> join(tokens[1], tokens[2]);
                case "observe" -> observe(tokens[1]);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> postLoginHelp();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void postLoginHelp() {

    }
}
