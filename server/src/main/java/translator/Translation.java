package translator;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class Translation {
    private static final Gson GSON = new Gson();

    public static <T> T fromJsonToObject(Context ctx, Class<T> classOfT) {
        System.out.println("Made it inside new class");
        return GSON.fromJson(ctx.body(), classOfT);
    }
}
