package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class BoardCreation {
    public void createBoard(ChessGame.TeamColor teamColor, ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        createHeaderList(out, teamColor);
        createRowList(out, teamColor, board);
        createHeaderList(out, teamColor);
    }


}
