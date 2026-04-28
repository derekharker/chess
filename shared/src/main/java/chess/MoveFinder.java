package chess;

import java.util.Collection;

public class MoveFinder {

    private final ChessBoard board;
    private final ChessPosition pos;
    private final ChessGame.TeamColor teamColor;
    private final Collection<ChessMove> moves;
    private final ChessPiece.PieceType pieceType;

    public MoveFinder(ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor, Collection<ChessMove> moves,
                      ChessPiece.PieceType pieceType) {
        this.board = board;
        this.pos = pos;
        this.teamColor = teamColor;
        this.moves = moves;
        this.pieceType = pieceType;
    }

    public void pieceLogic() {
        switch(pieceType) {
            case KING:
                moveDirSingle(1,0);
                moveDirSingle(1,1);
                moveDirSingle(1,-1);
                moveDirSingle(-1,1);
                moveDirSingle(-1,0);
                moveDirSingle(-1,-1);
                moveDirSingle(0,1);
                moveDirSingle(0,-1);
                break;
            case QUEEN:
                moveDirMultiple(1,1);
                moveDirMultiple(1,-1);
                moveDirMultiple(1,0);
                moveDirMultiple(-1,1);
                moveDirMultiple(-1,0);
                moveDirMultiple(-1,-1);
                moveDirMultiple(0,1);
                moveDirMultiple(0,-1);
                break;
            case KNIGHT:
                moveDirSingle(2,1);
                moveDirSingle(-2,1);
                moveDirSingle(2,-1);
                moveDirSingle(-2,-1);
                moveDirSingle(1,2);
                moveDirSingle(1,-2);
                moveDirSingle(-1,2);
                moveDirSingle(-1,-2);
                break;
            case ROOK:
                moveDirMultiple(1,0);
                moveDirMultiple(-1,0);
                moveDirMultiple(0,1);
                moveDirMultiple(0,-1);
                break;
            case BISHOP:
                moveDirMultiple(1,1);
                moveDirMultiple(1,-1);
                moveDirMultiple(-1,1);
                moveDirMultiple(-1,-1);
                break;
        }
    }

    private void moveDirSingle(int rowDirection, int colDirection) {

    }


}