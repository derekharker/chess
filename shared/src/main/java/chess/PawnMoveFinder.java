package chess;

import java.util.Collection;

public class PawnMoveFinder {

    private final ChessBoard board;
    private final ChessPosition pos;
    private final ChessGame.TeamColor teamColor;
    private final Collection<ChessMove> moves;

    public PawnMoveFinder(ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor,
                          Collection<ChessMove> moves) {
        this.board = board;
        this.pos = pos;
        this.teamColor = teamColor;
        this.moves = moves;
    }

    
}
