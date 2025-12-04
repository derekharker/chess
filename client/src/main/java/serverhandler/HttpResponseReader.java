package serverhandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public final class HttpResponseReader {

    private HttpResponseReader() {
        // utility class; prevent instantiation
    }

    public static String readBody(HttpURLConnection conn, boolean trimLines) throws IOException {
        InputStream stream = (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                ? conn.getInputStream()
                : conn.getErrorStream();

        if (stream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(trimLines ? line.trim() : line);
            }
        }

        return response.toString();
    }
}

