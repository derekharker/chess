package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;


public class BoardCreation {
    public void createBoard(ChessGame.TeamColor teamColor, ChessBoard board, Collection<ChessMove> validMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        createHeaderList(out, teamColor);
        createRowList(out, teamColor, board, validMoves);
        createHeaderList(out, teamColor);
    }

    private boolean isPiece(int row, int col, ChessBoard board) {
        if (board.getPiece(new ChessPosition(row, col)) == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isLight(int row, int col) {
        if (row % 2 == 0 && col % 2 != 0){
            return true;
        } else {
            return row % 2 != 0 && col % 2 == 0;
        }
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

    private static void setLight(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
    }

    private static void setDark(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
    }

    private static void setBackground(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private static void setWhiteText(PrintStream out) {
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlackText(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void drawEmptySquare(PrintStream out) {
        out.print(EMPTY);
        out.print(" ");
        out.print(EMPTY);
    }

    private void createRowList(PrintStream out, ChessGame.TeamColor teamColor, ChessBoard board, Collection<ChessMove> moves) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 8; i > 0; i--) {
                drawRow(out, i, board, teamColor, moves);
            }
        } else {
            for (int j = 1; j < 9; j++) {
                drawRow(out, j, board, teamColor, moves);
            }
        }
    }

    private void drawRow(PrintStream out, int row, ChessBoard board, ChessGame.TeamColor teamColor, Collection<ChessMove> validMove) {
        assert row > 0 && row < 9;

        printRowNum(out, row);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i < 9; i++) {
                printSquareInfo(out, row, i, board, validMove);
            }
        } else {
            for (int j = 8; j > 0; j--) {
                printSquareInfo(out, row, j, board, validMove);
            }
        }

        printRowNum(out, row);
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void printSquareInfo(PrintStream out, int i, int j, ChessBoard board, Collection<ChessMove> moves) {
        assert i > 0 && i < 9;

        //Choosing the BG
        if (moves != null) {
            if (isStartMove(i, j, moves)) {
                setStartPos(out);
            } else if (isPossibleMove(i, j, moves)) {
                checkSetEndPosDarkOrLight(i, j, out);
            } else {
                //if not, just the start pos
                checkSetDarkOrLight(i, j, out);
            }
        } else {
            checkSetDarkOrLight(i, j, out);
        }

        //Is piece or not
        if (isPiece(i, j, board)) {
            printPieceInfo(out, i, j, board);
        } else {
            drawEmptySquare(out);
        }
    }

    private boolean isPossibleMove(int row, int col, Collection<ChessMove> validMoves){
        ChessPosition endPosition = new ChessPosition(row, col);
        for (ChessMove move : validMoves){
            if (move.getEndPosition().equals(endPosition)){
                return true;
            }
        }
        return false;
    }

    private boolean isStartMove(int row, int col, Collection<ChessMove> validMoves){
        ChessPosition startPosition = new ChessPosition(row, col);
        for (ChessMove move : validMoves){
            if (move.getStartPosition().equals(startPosition)){
                return true;
            }
        }
        return false;
    }

    private static void setStartPos(PrintStream out){
        out.print(SET_BG_COLOR_BLUE);
    }

    private void checkSetDarkOrLight(int row, int col, PrintStream out){
        if (isLight(row, col)){
            setLight(out);
        } else {
            setDark(out);
        }
    }

    private void checkSetEndPosDarkOrLight(int row, int col, PrintStream out){
        if (isLight(row, col)){
            setLightEndPosition(out);
        } else {
            setDarkEndPosition(out);
        }
    }

    private static void setDarkEndPosition(PrintStream out){
        out.print(SET_BG_COLOR_GREEN);
    }

    private static void setLightEndPosition(PrintStream out){
        out.print(SET_BG_COLOR_GREEN);
    }

    private void printPieceInfo(PrintStream out, int i, int j, ChessBoard board) {
        ChessPosition pos = new ChessPosition(i, j);
        String pieceType = pieceLetter(board.getPiece(pos).getPieceType());

        ChessGame.TeamColor teamColor = board.getPiece(pos).getTeamColor();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            setWhiteText(out);
        } else {
            setBlackText(out);
        }

        out.print(EMPTY);
        out.print(pieceType);
        out.print(EMPTY);
    }

    private String pieceLetter(ChessPiece.PieceType type) {
        return switch (type) {
            case PAWN -> "P";
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case ROOK -> "R";
            case KNIGHT -> "N";
        };
    }

    private void printRowNum(PrintStream out, int i) {
        setBackground(out);
        out.print(EMPTY);
        out.print(i);
        out.print(EMPTY);
    }
}
