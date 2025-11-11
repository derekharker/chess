package translatorclient;

import com.google.gson.Gson;

public class ClientTranslation {
    private static final Gson GSON = new Gson();

    public static Object fromObjectToJson(Object result){
        return GSON.toJson(result);
    }

    public static <T> T fromJsontoObjectNotRequest(String string, Class<T> classOfT){
        return GSON.fromJson(string, classOfT);
    }
}
