package chess;

import java.util.*;

public class PawnMoveFinder {

    private final ChessBoard board;
    private final ChessPosition pos;
    private final Collection<ChessMove> moves;
    private final ChessGame.TeamColor teamColor;

    private final int row;
    private final int col;

    private static final List<ChessPiece.PieceType> PROMOTIONS = List.of(
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT
    );

    public PawnMoveFinder(ChessBoard board, ChessPosition pos,
                          Collection<ChessMove> moves,
                          ChessGame.TeamColor teamColor) {
        this.board = board;
        this.pos = pos;
        this.moves = moves;
        this.teamColor = teamColor;
        this.row = pos.getRow();
        this.col = pos.getColumn();
    }

    public void calculateMoves() {
        int forward = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        int startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoRow = (teamColor == ChessGame.TeamColor.WHITE) ? 7 : 2;

        handleForward(forward, startRow, promoRow);
        handleCaptures(forward, promoRow);
    }

    private void handleForward(int forward, int startRow, int promoRow) {
        int nextRow = row + forward;

        if (!inBounds(nextRow, col)) {
            return;
        }

        ChessPosition oneStep = new ChessPosition(nextRow, col);

        if (board.getPiece(oneStep) != null) {
            return;
        }

        if (row == promoRow) {
            addPromotions(oneStep);
        } else {
            moves.add(new ChessMove(pos, oneStep, null));

            // double move handled separately
            if (row == startRow) {
                int jumpRow = row + 2 * forward;
                ChessPosition twoStep = new ChessPosition(jumpRow, col);

                if (inBounds(jumpRow, col) && board.getPiece(twoStep) == null) {
                    moves.add(new ChessMove(pos, twoStep, null));
                }
            }
        }
    }

    private void handleCaptures(int forward, int promoRow) {
        int[] offsets = {-1, 1};

        for (int dc : offsets) {
            int newRow = row + forward;
            int newCol = col + dc;

            if (!inBounds(newRow, newCol)) {
                continue;
            }

            ChessPosition target = new ChessPosition(newRow, newCol);
            ChessPiece piece = board.getPiece(target);

            if (piece == null || piece.getTeamColor() == teamColor) {
                continue;
            }

            if (row == promoRow) {
                addPromotions(target);
            } else {
                moves.add(new ChessMove(pos, target, null));
            }
        }
    }

    private void addPromotions(ChessPosition target) {
        for (ChessPiece.PieceType type : PROMOTIONS) {
            moves.add(new ChessMove(pos, target, type));
        }
    }

    private boolean inBounds(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }
}