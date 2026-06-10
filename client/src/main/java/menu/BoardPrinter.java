package menu;

import chess.*;

import static ui.EscapeSequences.*;
import java.util.Collection;

public class BoardPrinter {
    public String drawWhiteBoard() {
        return drawBoard(true);
    }

    public String drawBlackBoard() {
        return drawBoard(false);
    }

    public String drawWhiteBoard(ChessGame game) {
        return drawBoard(game, true);
    }

    public String drawBlackBoard(ChessGame game) {
        return drawBoard(game, false);
    }

    public String drawWhiteBoard(ChessGame game, Collection<ChessPosition> highlightedSquares) {
        return drawBoard(game, true, highlightedSquares);
    }

    public String drawBlackBoard(ChessGame game, Collection<ChessPosition> highlightedSquares) {
        return drawBoard(game, false, highlightedSquares);
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

    private String drawBoard(ChessGame game, boolean whitePersp) {
        StringBuilder board = new StringBuilder();

        String[] files = whitePersp
                ? new String[]{"a", "b", "c", "d", "e", "f", "g", "h"}
                : new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};

        int startColumn = whitePersp ? 8 : 1;
        int endColumn = whitePersp ? 1 : 8;
        int step = whitePersp ? -1 : 1;

        addColumnLabels(board, files);

        for (int rank = startColumn; whitePersp ? rank >= endColumn : rank <= endColumn; rank += step) {
            board.append(" ").append(rank).append(" ");

            for (String file : files) {
                char fileChar = file.charAt(0);
                boolean lightSquare = isLightSquare(fileChar, rank);

                board.append(squareBackground(lightSquare));
                board.append(pieceTextColor(game, fileChar, rank));
                board.append(pieceAt(game, fileChar, rank));
                board.append(RESET_TEXT_COLOR);
                board.append(RESET_BG_COLOR);
            }

            board.append(" ").append(rank).append("\n");
        }

        addColumnLabels(board, files);
        return board.toString();
    }

    private String drawBoard(ChessGame game, boolean whitePersp, Collection<ChessPosition> highlightedSquares) {
        StringBuilder board = new StringBuilder();

        String[] files = whitePersp
                ? new String[]{"a", "b", "c", "d", "e", "f", "g", "h"}
                : new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};

        int startColumn = whitePersp ? 8 : 1;
        int endColumn = whitePersp ? 1 : 8;
        int step = whitePersp ? -1 : 1;

        addColumnLabels(board, files);

        for (int rank = startColumn; whitePersp ? rank >= endColumn : rank <= endColumn; rank += step) {
            board.append(" ").append(rank).append(" ");

            for (String file : files) {
                char fileChar = file.charAt(0);
                boolean lightSquare = isLightSquare(fileChar, rank);
                ChessPosition position = new ChessPosition(rank, fileChar - 'a' + 1);

                if (isHighlighted(position, highlightedSquares)) {
                    board.append(SET_BG_COLOR_YELLOW);
                } else {
                    board.append(squareBackground(lightSquare));
                }

                board.append(pieceTextColor(game, fileChar, rank));
                board.append(pieceAt(game, fileChar, rank));
                board.append(RESET_TEXT_COLOR);
                board.append(RESET_BG_COLOR);
            }

            board.append(" ").append(rank).append("\n");
        }

        addColumnLabels(board, files);
        return board.toString();
    }

    private boolean isHighlighted(ChessPosition position, Collection<ChessPosition> highlightedSquares) {
        return highlightedSquares != null && highlightedSquares.contains(position);
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

        return isWhitePiece(piece) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
    }

    private String pieceTextColor(ChessGame game, char file, int rank) {
        String piece = pieceAt(game, file, rank);

        if (piece.isBlank()) {
            return SET_TEXT_COLOR_WHITE;
        }

        return isWhitePiece(piece) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
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

    private String pieceAt(ChessGame game, char file, int rank) {
        int col = file - 'a' + 1;

        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(rank, col));

        if (piece == null) {
            return EMPTY;
        }

        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> white ? WHITE_KING : BLACK_KING;
            case QUEEN -> white ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> white ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> white ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> white ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}
