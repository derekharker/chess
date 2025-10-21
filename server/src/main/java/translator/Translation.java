package translator;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class Translation {
    private static final Gson gson = new Gson();

    public static String fromObjectToJson(Object result) {
        return gson.toJson(result);
    }

    public static <T> T fromJsonToObject(Context ctx, Class<T> classOfT) {
        System.out.println("Made it inside new class");
        return gson.fromJson(ctx.body(), classOfT);
    }
}
