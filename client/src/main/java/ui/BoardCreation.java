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

    private void createHeaderList(PrintStream out, ChessGame.TeamColor teamColor) {
        assert teamColor == ChessGame.TeamColor.WHITE || teamColor == ChessGame.TeamColor.BLACK;

        setBackground(out);
        drawEmptySquare(out);

        String[] headers = {"A", "B", "C", "D", "E", "F", "G", "H"};

        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 0; i < 8; ++i) {
                drawHeader(out, headers[i]);
            }
        } else {
            for (int j = 7; j >= 0; --j) {
                drawHeader(out, headers[j]);
            }
        }


    }
}
