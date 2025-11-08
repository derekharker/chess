package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;


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

        drawEmptySquare(out);
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void drawHeader(PrintStream out, String header) {
        out.print(EMPTY);
        out.print(header);
        out.print(EMPTY);
    }

    private void drawEmptySquare(PrintStream out) {
        out.print(EMPTY.repeat(3));
    }

    private void createRowList(PrintStream out, ChessGame.TeamColor teamColor, ChessBoard board) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 8; i > 0; i--) {
                drawRow(out, i, board, teamColor);
            }
        } else {
            for (int j = 1; j < 9; j++) {
                drawRow(out, j, board, teamColor);
            }
        }
    }


}
