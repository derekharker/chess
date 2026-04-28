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



}