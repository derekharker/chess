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

    public MoveFinder(chess.ChessBoard board, chess.ChessPosition pos, Collection<chess.ChessMove> moves,
                      chess.ChessGame.TeamColor teamColor) {
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

    }
}
