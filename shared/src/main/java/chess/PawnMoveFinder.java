package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PawnMoveFinder {

    private final ChessBoard board;
    private final ChessPosition pos;
    private final ChessGame.TeamColor teamColor;
    private final Collection<ChessMove> moves;
    private int row;
    private int col;
    private List<ChessPiece.PieceType> promotionPiece = Arrays.asList(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK,ChessPiece.PieceType.BISHOP);

    public PawnMoveFinder(ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor,
                          Collection<ChessMove> moves) {
        this.board = board;
        this.pos = pos;
        this.teamColor = teamColor;
        this.moves = moves;
        this.row = pos.getRow();
        this.col = pos.getColumn();
    }

    public void calculateMoves() {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            moveDirection(1,0);
            moveDirection(1,1);
            moveDirection(1,-1);
        } else {
            moveDirection(-1,0);
            moveDirection(-1,1);
            moveDirection(-1,-1);
        }
    }

    private void promotePawn(ChessPosition newPosition) {
        for (ChessPiece.PieceType piece : promotionPiece) {
            ChessMove move = new ChessMove(pos, newPosition, piece);
            moves.add(move);
        }
    }

    private void moveDirection(int rowMove, int colMove) {
        int newRowPos = row + rowMove;
        int newColPos = col + colMove;

        if (newRowPos > 8 || newColPos > 8 || newRowPos < 1 || newColPos < 1) {
            return;
        }

        boolean doubleMove = false;
        boolean promotion = false;

        if (teamColor == ChessGame.TeamColor.WHITE) {
            if (row == 2 && colMove == 0) {
                doubleMove = true;
            }
            if (row == 7) {
                promotion = true;
            }
        }

        if (teamColor == ChessGame.TeamColor.BLACK) {
            if (row == 7 && colMove == 0) {
                doubleMove = true;
            }
            if (row == 2) {
                promotion = true;
            }
        }

        ChessPosition newPos = new ChessPosition(newRowPos, newColPos);
        ChessPiece newPiece = board.getPiece(newPos);

        if (colMove == 0) {
            if (newPiece != null) {
                return;
            }
            if (promotion) {
                promotePawn(newPos);
            } else {
                ChessMove newMove = new ChessMove(pos, newPos, null);
                moves.add(newMove);
                if (doubleMove) {
                    ChessPosition doublePos = new ChessPosition(newRowPos + rowMove, newColPos);
                    ChessPiece testPiece = board.getPiece(doublePos);
                    if (testPiece == null) {
                        ChessMove newDoubleMove = new ChessMove(pos, doublePos, null);
                        moves.add(newDoubleMove);
                    }
                }
            }
        } else {
            if (newPiece != null && newPiece.getTeamColor() != teamColor) {
                if (promotion) {
                    promotePawn(newPos);
                } else {
                    ChessMove newMove = new ChessMove(pos, newPos, null);
                    moves.add(newMove);
                }
            }
        }
    }
}
