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
                case "quit" -> quitChess();
                default -> postLoginHelp();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String postLoginHelp() {
        return "If you want to create a game, enter: create <game name>\n" +
                "If you want to list all games currently in the database, enter: list\n" +
                "If you want to join a present game, enter: join <game number> <WHITE|BLACK>\n" +
                "If you want to observe a game happening, enter: observe <game number>\n" +
                "If you want to logout when done, enter: logout\n" +
                "If you want to quit chess, enter: quit\n" +
                "Enter help again if you want to see this beautiful menu again :)\n";
    }

    private String create(String name) {
        return "created game";
    }

    private String list() {
        return "list";
    }

    private String join(String name, String color) {

    }

    private String observe(String name) {

    }

    private String logout() {
        return "Logged out successfully";
    }

    private String quitChess() {
        return "Bye!";
    }
}
