package typehandler;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class ServerMessageTypeAdapter extends TypeAdapter<ServerMessage> {

    @Override
    public void write(JsonWriter jsonWriter, ServerMessage serverMessage) throws IOException {
        Gson gson = new Gson();

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> gson.getAdapter(LoadGameMessage.class).write(jsonWriter,
                    (LoadGameMessage) serverMessage);
            case ERROR -> gson.getAdapter(ErrorMessage.class).write(jsonWriter, (ErrorMessage) serverMessage);
            case NOTIFICATION -> gson.getAdapter(NotificationMessage.class).write(jsonWriter,
                    (NotificationMessage) serverMessage);
        }


    }

    @Override
    public ServerMessage read(JsonReader jsonReader) throws IOException {
        String msg = null;
        ChessGame game = null;
        ServerMessage.ServerMessageType type = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "message" -> msg = jsonReader.nextString();
                case "game" -> game = readChessGame(jsonReader);
                case "serverMessageType" -> type = ServerMessage.ServerMessageType.valueOf(jsonReader.nextString());
            }
        }

        jsonReader.endObject();

        if (type == null) {
            return null;
        }

        return switch (type) {
            case LOAD_GAME -> new LoadGameMessage(game);
            case NOTIFICATION -> new NotificationMessage(msg);
            case ERROR -> new ErrorMessage(msg);
        };
    }

    private ChessBoard readChessBoard(JsonReader jsonReader) throws IOException {
        ChessBoard board = new ChessBoard();
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("squares")) {
                board = readSquares(jsonReader);
            }
        }
        jsonReader.endObject();
        return board;
    }

    private ChessBoard readSquares(JsonReader jsonReader) throws IOException {
        ChessBoard board = new ChessBoard();
        jsonReader.beginArray();

        for (int i = 0; i < 8; i++) {
            jsonReader.beginArray();
            for (int j = 0; j < 8; j++) {
                if (jsonReader.peek() != JsonToken.NULL) {
                    board.addPiece(new ChessPosition(8-i, j+1), readChessPiece(jsonReader));
                } else {
                    jsonReader.nextNull();
                }
            }
            jsonReader.endArray();
        }

        jsonReader.endArray();
        return board;
    }

    private ChessGame readChessGame(JsonReader jsonReader) throws IOException {
        ChessGame game = new ChessGame();
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "teamTurn" -> game.setTeamTurn(ChessGame.TeamColor.valueOf(jsonReader.nextString()));
                case "board" -> game.setBoard(readChessBoard(jsonReader));
                case "gameOver" -> game.setGameOver(jsonReader.nextBoolean());
            }
        }

        jsonReader.endObject();
        return game;
    }

    private ChessPiece readChessPiece(JsonReader jsonReader) throws IOException {
        ChessPiece.PieceType type = null;
        ChessGame.TeamColor color = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "type" -> type = ChessPiece.PieceType.valueOf(jsonReader.nextString());
                case "teamColor" -> color = ChessGame.TeamColor.valueOf(jsonReader.nextString());
            }
        }
        jsonReader.endObject();
        return new ChessPiece(color, type);
    }
}
