package translatorclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import typehandler.ServerMessageTypeAdapter;
import websocket.messages.ServerMessage;

public class ClientTranslation {
    private static final Gson GSON = new Gson();

    private static Gson prepareGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new ServerMessageTypeAdapter());
        return builder.create();
    }

    public static Object fromObjectToJson(Object result){
        return GSON.toJson(result);
    }

    public static <T> T fromJsontoObjectNotRequest(String string, Class<T> classOfT){
        return GSON.fromJson(string, classOfT);
    }
}
