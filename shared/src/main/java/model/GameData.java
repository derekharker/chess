package model;

import chess.ChessGame;

public class GameData {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;
    ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;

        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.game = game;
        this.blackUsername = blackUsername;


    }
    public String getBlackUsername() {
        return blackUsername;
    }
    public String getWhiteUsername() {
        return whiteUsername;

    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }
    public int getGameID() {
        return gameID;
    }

    public ChessGame getGame() {
        return game;
    }

}
