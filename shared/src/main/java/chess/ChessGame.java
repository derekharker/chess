package chess;

import java.util.*;

public class ChessGame {

    private TeamColor currentTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        validateMove(move);

        applyMove(board, move);

        currentTurn = opposite(currentTurn);
    }

    public Collection<ChessMove> validMoves(ChessPosition start) {
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> legal = new ArrayList<>();

        for (ChessMove move : piece.pieceMoves(board, start)) {
            ChessBoard simulation = duplicateBoard();

            try {
                applyMove(simulation, move);
                if (!isCheck(piece.getTeamColor(), simulation)) {
                    legal.add(move);
                }

            } catch (InvalidMoveException ignored) {
            }
        }

        return legal;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return isCheck(teamColor, board);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor)
                && collectMoves(teamColor).isEmpty();
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return currentTurn == teamColor
                && !isInCheck(teamColor)
                && collectMoves(teamColor).isEmpty();
    }

    private void validateMove(ChessMove move) throws InvalidMoveException {

        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Wrong turn");
        }

        Collection<ChessMove> legal = validMoves(move.getStartPosition());

        if (legal == null || !legal.contains(move)) {
            throw new InvalidMoveException("Illegal move");
        }
    }

    private void applyMove(ChessBoard targetBoard,
                           ChessMove move) throws InvalidMoveException {

        ChessPiece source = targetBoard.getPiece(move.getStartPosition());

        if (source == null) {
            throw new InvalidMoveException("Missing piece");
        }

        ChessPiece.PieceType type =
                move.getPromotionPiece() != null
                        ? move.getPromotionPiece()
                        : source.getPieceType();

        ChessPiece moved =
                new ChessPiece(source.getTeamColor(), type);

        targetBoard.removePiece(move.getStartPosition());
        targetBoard.addPiece(move.getEndPosition(), moved);
    }

    private Collection<ChessMove> collectMoves(TeamColor color) {

        Collection<ChessMove> moves = new ArrayList<>();

        forEachPiece((position, piece) -> {
            if (piece.getTeamColor() == color) {
                Collection<ChessMove> pieceMoves = validMoves(position);

                if (pieceMoves != null) {
                    moves.addAll(pieceMoves);
                }
            }
        });

        return moves;
    }

    private boolean isCheck(TeamColor teamColor, ChessBoard activeBoard) {

        ChessPosition king = locateKing(teamColor, activeBoard);

        if (king == null) {
            return false;
        }

        final boolean[] inCheck = {false};

        scanBoard(activeBoard, (position, piece) -> {

            if (piece.getTeamColor() == teamColor) {
                return;
            }

            for (ChessMove move : piece.pieceMoves(activeBoard, position)) {

                if (move.getEndPosition().equals(king)) {
                    inCheck[0] = true;
                    return;
                }
            }
        });
        //return if not in check here
        return inCheck[0];
    }

    private ChessPosition locateKing(TeamColor color,
                                     ChessBoard activeBoard) {

        ChessPiece target = new ChessPiece(color, ChessPiece.PieceType.KING);

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = activeBoard.getPiece(pos);
                if (target.equals(piece)) {
                    return pos;
                }
            }
        }

        return null;
    }

    private ChessBoard duplicateBoard() {

        ChessBoard clone = new ChessBoard();

        scanBoard(board, (position, piece) -> {

            clone.addPiece(
                    position,
                    new ChessPiece(
                            piece.getTeamColor(),
                            piece.getPieceType()
                    )
            );
        });

        return clone;
    }

    private TeamColor opposite(TeamColor color) {
        return color == TeamColor.WHITE
                ? TeamColor.BLACK
                : TeamColor.WHITE;
    }

    private void forEachPiece(BoardAction action) {
        scanBoard(board, action);
    }

    private void scanBoard(ChessBoard activeBoard,
                           BoardAction action) {

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition pos =
                        new ChessPosition(row, col);

                ChessPiece piece =
                        activeBoard.getPiece(pos);

                if (piece != null) {
                    action.handle(pos, piece);
                }
            }
        }
    }

    @FunctionalInterface
    private interface BoardAction {
        void handle(ChessPosition position,
                    ChessPiece piece);
    }

    package chess;

import java.util.*;

    public class ChessGame {

        private TeamColor currentTurn = TeamColor.WHITE;
        private ChessBoard board = new ChessBoard();

        public ChessGame() {
            board.resetBoard();
        }

        public enum TeamColor {
            WHITE,
            BLACK
        }

        public TeamColor getTeamTurn() {
            return currentTurn;
        }

        public void setTeamTurn(TeamColor team) {
            currentTurn = team;
        }

        public ChessBoard getBoard() {
            return board;
        }

        public void setBoard(ChessBoard board) {
            this.board = board;
        }

        public void makeMove(ChessMove move) throws InvalidMoveException {
            validateMove(move);

            applyMove(board, move);

            currentTurn = opposite(currentTurn);
        }

        public Collection<ChessMove> validMoves(ChessPosition start) {
            ChessPiece piece = board.getPiece(start);

            if (piece == null) {
                return null;
            }

            Collection<ChessMove> legal = new ArrayList<>();

            for (ChessMove move : piece.pieceMoves(board, start)) {
                ChessBoard simulation = duplicateBoard();

                try {
                    applyMove(simulation, move);

                    if (!isCheck(piece.getTeamColor(), simulation)) {
                        legal.add(move);
                    }

                } catch (InvalidMoveException ignored) {
                }
            }

            return legal;
        }

        public boolean isInCheck(TeamColor teamColor) {
            return isCheck(teamColor, board);
        }

        public boolean isInCheckmate(TeamColor teamColor) {
            return isInCheck(teamColor)
                    && collectMoves(teamColor).isEmpty();
        }

        public boolean isInStalemate(TeamColor teamColor) {
            return currentTurn == teamColor
                    && !isInCheck(teamColor)
                    && collectMoves(teamColor).isEmpty();
        }

        private void validateMove(ChessMove move) throws InvalidMoveException {

            ChessPiece piece = board.getPiece(move.getStartPosition());

            if (piece == null) {
                throw new InvalidMoveException("No piece at start position");
            }

            if (piece.getTeamColor() != currentTurn) {
                throw new InvalidMoveException("Wrong turn");
            }

            Collection<ChessMove> legal = validMoves(move.getStartPosition());

            if (legal == null || !legal.contains(move)) {
                throw new InvalidMoveException("Illegal move");
            }
        }

        private void applyMove(ChessBoard board, ChessMove move)
                throws InvalidMoveException {

            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece == null) {
                throw new InvalidMoveException("No piece to move");
            }

            ChessPiece.PieceType newType = piece.getPieceType();
            if (move.getPromotionPiece() != null) {
                newType = move.getPromotionPiece();
            }

            ChessPiece movedPiece =
                    new ChessPiece(piece.getTeamColor(), newType);

            board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), movedPiece);
        }

        private Collection<ChessMove> collectMoves(TeamColor color) {

            Collection<ChessMove> moves = new ArrayList<>();

            forEachPiece((position, piece) -> {

                if (piece.getTeamColor() == color) {
                    Collection<ChessMove> pieceMoves = validMoves(position);

                    if (pieceMoves != null) {
                        moves.addAll(pieceMoves);
                    }
                }
            });

            return moves;
        }

        private boolean isCheck(TeamColor teamColor, ChessBoard activeBoard) {

            ChessPosition king = locateKing(teamColor, activeBoard);

            if (king == null) {
                return false;
            }

            final boolean[] threatened = {false};

            scanBoard(activeBoard, (position, piece) -> {

                if (piece.getTeamColor() == teamColor) {return;}
                for (ChessMove move : piece.pieceMoves(activeBoard, position)) {

                    if (move.getEndPosition().equals(king)) {
                        threatened[0] = true;
                        return;
                    }
                }
            });

            return threatened[0];
        }

        private ChessPosition locateKing(TeamColor color,
                                         ChessBoard activeBoard) {

            ChessPiece target =
                    new ChessPiece(color, ChessPiece.PieceType.KING);

            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition pos = new ChessPosition(r, c);
                    ChessPiece piece = activeBoard.getPiece(pos);

                    if (target.equals(piece)) {
                        return pos;
                    }
                }
            }

            return null;
        }

        private ChessBoard duplicateBoard() {

            ChessBoard clone = new ChessBoard();
            scanBoard(board, (position, piece) -> {

                clone.addPiece(
                        position,
                        new ChessPiece(
                                piece.getTeamColor(),
                                piece.getPieceType()
                        )
                );
            });

            return clone;
        }

        private TeamColor opposite(TeamColor color) {
            return color == TeamColor.WHITE
                    ? TeamColor.BLACK
                    : TeamColor.WHITE;
        }

        private void forEachPiece(BoardAction action) {
            scanBoard(board, action);
        }

        private void scanBoard(ChessBoard activeBoard,
                               BoardAction action) {

            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition pos = new ChessPosition(row, col);
                    ChessPiece piece = activeBoard.getPiece(pos);

                    if (piece != null) {
                        action.handle(pos, piece);
                    }
                }
            }
        }

        @FunctionalInterface
        private interface BoardAction {
            void handle(ChessPosition position,
                        ChessPiece piece);
        }

        @Override
        public String toString() {
            return "ChessGame{" +
                    "currentTurn=" + currentTurn +
                    ", board=" + board +
                    '}';
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;

            if (!(o instanceof ChessGame game)) {
                return false;
            }

            return currentTurn == game.currentTurn
                    && Objects.equals(board, game.board);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentTurn, board);
        }
    }
}