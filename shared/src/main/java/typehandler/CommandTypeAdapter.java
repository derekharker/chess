package typehandler;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;

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

}
