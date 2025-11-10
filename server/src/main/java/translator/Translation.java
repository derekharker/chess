package translator;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class Translation {
    private static final Gson GSON = new Gson();

    public static <T> T fromJsonToObject(Context ctx, Class<T> classOfT) {
        return GSON.fromJson(ctx.body(), classOfT);
    }

    public static <T> T fromJsontoObjectNotRequest(String str, Class<T> type) {
        return GSON.fromJson(str, type);
    }

    public static Object fromObjectToJson(Object result){
        return GSON.toJson(result);
    }
}
