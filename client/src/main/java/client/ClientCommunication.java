package client;

import com.google.gson.Gson;
import ui.ClientException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ClientCommunication {
    private final String serverURL;
    private final Gson gson = new Gson();

    public ClientCommunication(int port) {
        this.serverURL = "http://localhost:" + port;
    }

    public <T> T get(String path, String authToken, Class<T> responseClass) throws ClientException {
        return makeRequest("GET", path, null, authToken, responseClass);
    }

    public <T> T post(String path, Object requestBody, String authToken, Class<T> responseClass) throws ClientException {
        return makeRequest("POST", path, requestBody, authToken, responseClass);
    }

    public <T> T put(String path, Object requestBody, String authToken, Class<T> responseClass) throws ClientException {
        return makeRequest("PUT", path, requestBody, authToken, responseClass);
    }

    public <T> T delete(String path, String authToken, Class<T> responseClass) throws ClientException {
        return makeRequest("DELETE", path, null, authToken, responseClass);
    }

    private <T> T makeRequest(
            String method,
            String path,
            Object requestBody,
            String authToken,
            Class<T> responseClass
    ) throws ClientException {
        try {
            // creating url and exception handling stuff
            var url = URI.create(serverURL + path).toURL();


            var connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (authToken != null && !authToken.isBlank()) {
                connection.setRequestProperty("Authorization", authToken);
            }

            if (requestBody != null) {
                connection.setDoOutput(true);
                writeRequestBody(connection, requestBody);
            }

            connection.connect();

            var statusCode = connection.getResponseCode();

            if (statusCode < 200 || statusCode >= 300) {
                throw new ClientException(readErrorMessage(connection));
            }

            if (responseClass == null || responseClass == Void.class) {
                return null;
            }

            //return final bodyerror me=sg
            return readResponseBody(connection, responseClass);


        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientException("Unable to talk to the server on MySQL ; sorry");
        }
    }

    private void writeRequestBody(HttpURLConnection conn, Object reqBody) throws IOException {
        String json = gson.toJson(reqBody);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private <T> T readResponseBody(HttpURLConnection connection, Class<T> responseClass) throws IOException {
        try (InputStream inputStream = connection.getInputStream()) {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            if (json.isBlank()) {
                return null;
            }

            return gson.fromJson(json, responseClass);
        }
    }
    //when we have msg to error url
    private String readErrorMessage(HttpURLConnection connection) {
        try (InputStream errorStream = connection.getErrorStream()) {
            if (errorStream == null) {
                return "Request failed.";
            }
// json eror stream
            String json = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);

            if (json.isBlank()) {
                return "Request failed.";
            }
            ErrorResponse errorResponse = gson.fromJson(json, ErrorResponse.class);

            if (errorResponse != null && errorResponse.message() != null && !errorResponse.message().isBlank()) {
                return errorResponse.message();
            }
            return "Request failed.";
        } catch (Exception e) {
            return "Request failed.";
        }
    }

    private record ErrorResponse(String message) {
    }
}
