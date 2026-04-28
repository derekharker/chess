package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MoveFinder {

    private final ChessBoard board;
    private final ChessPosition pos;
    private final Collection<ChessMove> moves;
    private final ChessGame.TeamColor teamColor;
    private int row;
    private int col;
    private List<ChessPiece.PieceType> promotionP = Arrays.asList(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP);

    public MoveFinder(ChessBoard board, ChessPosition pos, Collection<ChessMove> moves,
                      ChessGame.TeamColor teamColor) {
        this.board = board;
        this.pos = pos;
        this.moves = moves;
        this.teamColor = teamColor;
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
        for (ChessPiece.PieceType piece : promotionP) {
            ChessMove move = new ChessMove(pos, newPosition, piece);
            moves.add(move);
        }
    }

    private void moveDirection(int rowMove, int colMove) {
        int newRowPos = row + rowMove;
        int newColPos = col + colMove;

        if (newRowPos > 8 || newColPos > 8 ||  newRowPos < 1 || newColPos < 1) {
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
            if (newPiece == null) {
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
