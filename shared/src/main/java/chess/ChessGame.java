package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    public ChessBoard copy() {
        ChessBoard out = new ChessBoard();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition p = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(p);
                if (piece != null) {
                    out.addPiece(p, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return out;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        ChessGame.TeamColor teamColor = board.getPiece(startPosition).getTeamColor();

        Iterator<ChessMove> iterator = validMoves.iterator();
        for (; iterator.hasNext(); ) {
            ChessMove move = iterator.next();
            ChessBoard clone = this.copy();
            try {
                performMove(move, clone); // implement
                if (checkHelp(teamColor, clone)) {
                    iterator.remove();
                }
            } catch(InvalidMoveException e) {
                throw new RuntimeException(e);
            }
        }

        return validMoves;
    }

    private boolean checkHelp(ChessGame.TeamColor teamColor, ChessBoard board) {
        ChessPosition kingP = getKingP(teamColor, board);
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition tryP = new ChessPosition(row, col);
                ChessPiece curr = board.getPiece(tryP);
                if (curr != null && curr.getTeamColor() != teamColor) {
                    Collection<ChessMove> enemyMoves = curr.pieceMoves(board, tryP);
                    for (ChessMove enemyMove : enemyMoves) {
                        if (enemyMove.getEndPosition().equals(kingP)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition getKingP(ChessGame.TeamColor teamColor, ChessBoard board) {
        ChessPiece tempK = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition kingP = new ChessPosition(i, j);
                if (board.getPiece(kingP) != null && board.getPiece(kingP).equals(tempK)) {
                    return kingP;
                }
            }
        }
        return null;
    }

    private void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {

        ChessPiece.PieceType pieceType;
        ChessGame.TeamColor teamColor = board.getPiece(move.getStartPosition()).getTeamColor();

        if (move.getPromotionPiece() == null) {
            pieceType = board.getPiece(move.getStartPosition()).getPieceType();
        } else {
            pieceType = move.getPromotionPiece();
        }

        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), new ChessPiece(teamColor, pieceType));
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!moveChecks(move)) {return;}
        performMove(move, board);
        try {
            endCurrentTurn();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean moveChecks(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("There's no piece there.");
        }
        ChessGame.TeamColor teamColor = board.getPiece(move.getStartPosition()).getTeamColor();
        if (getTeamTurn() != teamColor) {
            throw new InvalidMoveException("Not your turn!");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Move not valid.");
        }

        return true;
    }

    private void endCurrentTurn() throws CloneNotSupportedException {
        if (getTeamTurn() == ChessGame.TeamColor.WHITE) {
            setTeamTurn(ChessGame.TeamColor.BLACK);
        } else {
            setTeamTurn(ChessGame.TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkHelp(teamColor, this.board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            Collection<ChessMove> allFriendly = findValidMoves(teamColor);
            if (allFriendly.size() > 0) {
                return false;
            } else {
                return true;
            }
        } else { return false;}
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (getTeamTurn() != teamColor) {
            return false;
        }
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> moves = findValidMoves(teamColor);
        if (moves.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    private Collection<ChessMove> findValidMoves(ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> tmp = validMoves(pos);
                    for (ChessMove move : tmp) {
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


}
