package response;

import java.util.Collection;

public record ListGamesResponse(Collection<ListedGame> games, String message) {
    public record ListedGame(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName
    ) {}
}
