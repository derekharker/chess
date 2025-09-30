package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clearBoard();
        addAllPawns();
        addPieceBoard(ChessPiece.PieceType.BISHOP, 2, 5);
        addPieceBoard(ChessPiece.PieceType.KING, 4, 0);
        addPieceBoard(ChessPiece.PieceType.QUEEN, 3, 0);
        addPieceBoard(ChessPiece.PieceType.ROOK, 0, 7);
        addPieceBoard(ChessPiece.PieceType.KNIGHT, 1, 6);
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
    }

    private void addAllPawns() {
        for (int col = 0; col < 8; col++) {
            board[6][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
    }

    private void addPieceBoard(ChessPiece.PieceType type, int col1, int col2) {
        if (type == ChessPiece.PieceType.KING || type == ChessPiece.PieceType.QUEEN) {
            board[0][col1] = new ChessPiece(ChessGame.TeamColor.WHITE, type);
            board[7][col1] = new ChessPiece(ChessGame.TeamColor.BLACK, type);
        } else {
            board[0][col1] = new ChessPiece(ChessGame.TeamColor.WHITE, type);
            board[0][col2] = new ChessPiece(ChessGame.TeamColor.WHITE, type);
            board[7][col1] = new ChessPiece(ChessGame.TeamColor.BLACK, type);
            board[7][col2] = new ChessPiece(ChessGame.TeamColor.BLACK, type);
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{"
                + "\n" + Arrays.toString(board[0])
                + "\n" + Arrays.toString(board[1])
                + "\n" + Arrays.toString(board[2])
                + "\n" + Arrays.toString(board[3])
                + "\n" + Arrays.toString(board[4])
                + "\n" + Arrays.toString(board[5])
                + "\n" + Arrays.toString(board[6])
                + "\n" + Arrays.toString(board[7]) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
