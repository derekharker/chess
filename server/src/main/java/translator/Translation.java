package translator;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class Translation {
    private static final Gson GSON = new Gson();

    //unused method deleted here

    public static <T> T fromJsontoObjectNotRequest(String str, Class<T> type) {
        return GSON.fromJson(str, type);
    }

    public static Object fromObjectToJson(Object result){
        return GSON.toJson(result);
    }
}
