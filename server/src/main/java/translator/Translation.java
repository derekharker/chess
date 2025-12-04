package translator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import typehandler.CommandTypeAdapter;
import websocket.commands.UserGameCommand;

public class Translation {
    private final static Gson GSON = prepareGson();

    public static <T> T fromJsonToObject(Context ctx, Class<T> classOfT) {
        return GSON.fromJson(ctx.body(), classOfT);
    }

    public static <T> T fromJsontoObjectNotRequest(String str, Class<T> type) {
        return GSON.fromJson(str, type);
    }

    public static Object fromObjectToJson(Object result){
        return GSON.toJson(result);
    }

    private static Gson prepareGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class, new CommandTypeAdapter());
        return builder.create();
    }
}
