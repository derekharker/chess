package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

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
        } else return row % 2 != 0 && col % 2 == 0;
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

    private void drawRow(PrintStream out, int row, ChessBoard board, ChessGame.TeamColor teamColor) {
        assert row > 0 && row < 9;

        printRowNum(out, row);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i < 9; i++) {
                printSquareInfo(out, row, i, board);
            }
        } else {
            for (int j = 8; j > 0; j--) {
                printSquareInfo(out, row, j, board);
            }
        }

        printRowNum(out, row);
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void printSquareInfo(PrintStream out, int i, int j, ChessBoard board) {
        assert i > 0 && i < 9;

        //Choosing the BG
        if (isLight(i, j)) {
            setLight(out);
        } else {
            setDark(out);
        }

        //Is piece or not
        if (isPiece(i, j, board)) {
            printPieceInfo(out, i, j, board);
        } else {
            out.print(EMPTY.repeat(3));
        }
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
