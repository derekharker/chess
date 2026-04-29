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
        int[][] directions;
        boolean repeat;

        switch (pieceType) {
            case KING -> {
                directions = new int[][]{
                        {1,0},{1,1},{1,-1},{-1,1},{-1,0},{-1,-1},{0,1},{0,-1}
                };
                repeat = false;
            }
            case QUEEN -> {
                directions = new int[][]{
                        {1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}
                };
                repeat = true;
            }
            case ROOK -> {
                directions = new int[][]{
                        {1,0},{-1,0},{0,1},{0,-1}
                };
                repeat = true;
            }
            case BISHOP -> {
                directions = new int[][]{
                        {1,1},{1,-1},{-1,1},{-1,-1}
                };
                repeat = true;
            }
            case KNIGHT -> {
                directions = new int[][]{
                        {2,1},{-2,1},{2,-1},{-2,-1},
                        {1,2},{1,-2},{-1,2},{-1,-2}
                };
                repeat = false;
            }
            default -> { return; }
        }

        for (int[] d : directions) {
            if (repeat) {
                moveDirMultiple(d[0], d[1]);
            } else {
                moveDirSingle(d[0], d[1]);
            }
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