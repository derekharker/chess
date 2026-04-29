package chess;

import java.util.Arrays;
import java.util.Objects;

public class ChessBoard {

    //Because of the borders as well
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[toIndex(position.getRow())][toIndex(position.getColumn())] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return board[toIndex(position.getRow())][toIndex(position.getColumn())];
    }

    public void resetBoard() {
        wipeBoaard();
        ChessPiece.PieceType[] layout = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        placeRow(0, ChessGame.TeamColor.WHITE, layout);
        placeRow(1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        placeRow(7, ChessGame.TeamColor.BLACK, layout);
        placeRow(6, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
    }

    private void wipeBoaard() {
        for (ChessPiece[] row : board) {
            Arrays.fill(row, null);
        }
    }

    private void placeRow(int row, ChessGame.TeamColor color, ChessPiece.PieceType[] layout) {
        for (int col = 0; col < layout.length; col++) {
            board[row][col] = new ChessPiece(color, layout[col]);
        }
    }

    private void placeRow(int row, ChessGame.TeamColor color, ChessPiece.PieceType type) {
        for (int col = 0; col < 8; col++) {
            board[row][col] = new ChessPiece(color, type);
        }
    }

    private int toIndex(int coord) {
        return coord - 1;
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}