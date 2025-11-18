package typehandler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
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

    private ChessGame readChessGame(JsonReader jsonReader) throws IOException {

    }

}
