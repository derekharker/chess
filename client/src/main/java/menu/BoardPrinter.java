package menu;

import ui.EscapeSequences.*;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    public String drawWhiteBoard() {
        return drawBoard(true);
    }

    public String drawBlackBoard() {
        return drawBoard(false);
    }

    private String drawBoard(boolean whitePersp) {
        StringBuilder board = new StringBuilder();

        String[] files = whitePersp
                ? new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} :
                new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};

        int startColum = whitePersp ? 8 : 1;
        int endColum = whitePersp ? 1 : 8;
        int step = whitePersp ? -1 : 1;

        addColumnLabels(board, files);

        for (int i = startColum; whitePersp ? i >= endColum : i <= endColum; i += step) {
            board.append(" ").append(i).append(" ");

            for (String file : files) {
                //here with first character
                char fileChar = file.charAt(0);
                boolean lightSquare = isLightSquare(fileChar, i);

                board.append(squareBackground(lightSquare));
                board.append(pieceTextColor(fileChar, i));
                board.append(pieceAt(fileChar, i));
                board.append(RESET_TEXT_COLOR);
                board.append(RESET_BG_COLOR);
            }

            board.append(" ").append(i).append("\n");
        }
        addColumnLabels(board, files);

        return board.toString();
    }

    private void addColumnLabels(StringBuilder board, String[] files) {
        board.append("   ");
        for (String file : files) {
            board.append(" ").append(file).append(" ");
        }
        board.append("\n");
    }

    private String squareBackground(boolean lightSquare) {
        return lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREEN;
    }

    private String pieceTextColor(char file, int rank) {
        String piece = pieceAt(file, rank);

        if (piece.isBlank()) {
            return SET_TEXT_COLOR_WHITE;
        }

        return isWhitePiece(piece) ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
    }

    private boolean isWhitePiece(String piece) {
        return piece.equals(WHITE_KING) ||
                piece.equals(WHITE_QUEEN) || piece.equals(WHITE_BISHOP) || piece.equals(WHITE_KNIGHT) ||
                piece.equals(WHITE_ROOK) || piece.equals(WHITE_PAWN);
    }

    private boolean isLightSquare(char row, int i) {
        int rowNumber = row - 'a' + 1;

        //flip flop of light and dark - help here
        return (rowNumber + i) % 2 == 1;
    }

    private String pieceAt(char file, int rank) {
        if (rank == 2) {
            return WHITE_PAWN;
        }

        if (rank == 7) {
            return BLACK_PAWN;
        }

        if (rank == 1) {
            return switch (file) {
                case 'a', 'h' -> WHITE_ROOK;
                case 'b', 'g' -> WHITE_KNIGHT;
                case 'c', 'f' -> WHITE_BISHOP;
                case 'd' -> WHITE_QUEEN;
                case 'e' -> WHITE_KING;
                default -> EMPTY;
            };
        }

        if (rank == 8) {
            return switch (file) {
                case 'a', 'h' -> BLACK_ROOK;
                case 'b', 'g' -> BLACK_KNIGHT;
                case 'c', 'f' -> BLACK_BISHOP;
                case 'd' -> BLACK_QUEEN;
                case 'e' -> BLACK_KING;
                default -> EMPTY;
            };
        }

        return EMPTY;
    }
}
