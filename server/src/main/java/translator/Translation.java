package translator;

import com.google.gson.Gson;
import spark.Request;

public class Translation {
    private static Gson gson = new Gson();

    public static Object fromObjectToJson(Object result){
        return gson.toJson(result);
    }

    public static <T> T fromJsonToObject(Request request, Class<T> classOfT) {
        System.out.println("Made it inside new class");
        return gson.fromJson(request.body(), classOfT);
    }
}
