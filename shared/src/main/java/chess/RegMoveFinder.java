package chess;

import java.util.Collection;

public class RegMoveFinder {

    private final ChessBoard board;
    private final ChessPosition pos;
    private final ChessGame.TeamColor teamColor;
    private final Collection<ChessMove> moves;
    private final ChessPiece.PieceType pieceType;

    public RegMoveFinder(ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor,
                         Collection<ChessMove> moves, ChessPiece.PieceType pieceType) {
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
                moveDirSingle(0,1);
                moveDirSingle(0,-1);
                moveDirSingle(-1,0);
                moveDirSingle(-1,1);
                moveDirSingle(-1,-1);
                break;
            case QUEEN:
                moveDirMultiple(1,1);
                moveDirMultiple(1,0);
                moveDirMultiple(0,-1);
                moveDirMultiple(-1,0);
                moveDirMultiple(0,1);
                moveDirMultiple(-1,1);
                moveDirMultiple(-1,-1);
                moveDirMultiple(1,-1);
                break;
            case BISHOP:
                moveDirMultiple(-1,-1);
                moveDirMultiple(1,-1);
                moveDirMultiple(1,1);
                moveDirMultiple(-1,1);
                break;
            case ROOK:
                moveDirMultiple(0,-1);
                moveDirMultiple(0,1);
                moveDirMultiple(-1,0);
                moveDirMultiple(1,0);
                break;
            case KNIGHT:
                moveDirSingle(2,1);
                moveDirSingle(2,-1);
                moveDirSingle(1,2);
                moveDirSingle(1,-2);
                moveDirSingle(-1,2);
                moveDirSingle(-1,-2);
                moveDirSingle(-2,1);
                moveDirSingle(-2,-1);
                break;
        }
    }

    private void moveDirSingle(int rowDirection, int colDirection) {
        int tempRow = this.pos.getRow();
        int tempCol = this.pos.getColumn();

        tempRow += (rowDirection);
        tempCol += (colDirection);

        if (! inbounds(tempRow, tempCol)) return;

        ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);

        if (board.getPiece(tempPosition) == null) {
            moves.add(new ChessMove(pos, tempPosition, null));
        } else if (board.getPiece(tempPosition).getTeamColor() != teamColor) {
            moves.add(new ChessMove(pos, tempPosition, null));
        } else {return;}
    }

    public boolean inbounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private void moveDirMultiple(int rowDirection, int colDirection) {
        int tempRow = this.pos.getRow();
        int tempCol = this.pos.getColumn();

        while (inbounds(tempRow, tempCol)) {
            tempRow += (rowDirection);
            tempCol += (colDirection);

            if (! inbounds(tempRow, tempCol)) {
                break;
            }

            ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);

            if (board.getPiece(tempPosition) == null) {
                moves.add(new ChessMove(pos, tempPosition, null));
            } else if (board.getPiece(tempPosition).getTeamColor() != teamColor) {
                moves.add(new ChessMove(pos, tempPosition, null));
                break;
            } else {break;}

        }
    }
}
