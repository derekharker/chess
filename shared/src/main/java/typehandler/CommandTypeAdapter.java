package typehandler;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import websocket.commands.*;

import java.io.IOException;

public class CommandTypeAdapter extends TypeAdapter<UserGameCommand> {
    @Override
    public void write(JsonWriter jsonWriter, UserGameCommand userGameCommand) throws IOException {
        Gson gson = new Gson();

        switch(userGameCommand.getCommandType()) {
            case LEAVE -> gson.getAdapter(LeaveGameCommand.class).write(jsonWriter,
                    (LeaveGameCommand) userGameCommand);
            case CONNECT -> gson.getAdapter(ConnectCommand.class).write(jsonWriter,
                    (ConnectCommand) userGameCommand);
            case RESIGN -> gson.getAdapter(ResignCommand.class).write(jsonWriter,
                    (ResignCommand) userGameCommand);
        }
    }

    @Override
    public UserGameCommand read(JsonReader jsonReader) throws IOException {
        String authToken = null;
        ChessGame.TeamColor teamColor = null;
        int gameID = 0;
        UserGameCommand.CommandType commandType = null;
        ChessMove move = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "authToken" -> authToken = jsonReader.nextString();
                case "gameID" -> gameID = jsonReader.nextInt();
                case "commandType" -> commandType = UserGameCommand.CommandType.valueOf(jsonReader.nextString());
                case "move" -> move = readChessMove(jsonReader);

            }
        }

        jsonReader.endObject();

        if(commandType == null) {
            return null;
        } else {
            return switch (commandType) {
                case CONNECT -> new ConnectCommand(authToken, gameID);
                case MAKE_MOVE -> new MakeMoveCommand(authToken, gameID, move);
                case LEAVE -> new LeaveGameCommand(authToken, gameID);
                case RESIGN -> new ResignCommand(authToken, gameID);
            };
        }
    }

    private ChessMove readChessMove(JsonReader jsonReader) throws IOException {
        ChessPiece.PieceType promotion = null;
        ChessPosition start = null;
        ChessPosition end = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "startPosition" -> start = readChessPosition(jsonReader);
                case "promotionPiece" -> promotion = ChessPiece.PieceType.valueOf(jsonReader.nextString());
                case "endPosition" -> end = readChessPosition(jsonReader);
            }
        }


        jsonReader.endObject();
        return new ChessMove(start, end, promotion);
    }

    private ChessPosition readChessPosition(JsonReader jsonReader) throws IOException {
        int row = 0;
        int col = 0;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "col" -> col = jsonReader.nextInt();
                case "row" -> row = jsonReader.nextInt();
            }
        }

        jsonReader.endObject();
        return new ChessPosition(row, col);
    }
}
