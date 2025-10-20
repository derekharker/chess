import server.Server;
import chess.*;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);
        var piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        System.out.println("♕ 240 Chess Server" + piece);
    }
}