package serverhandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientCommunication {
    public String doPost(String urlString, String body, String authToken) throws IOException, IOException {
        HttpURLConnection connection = getHttpURLConnection(urlString, authToken, "POST", true);
        sendRequest(connection, body);
        return getResponseBody(connection);
    }

    private static HttpURLConnection getHttpURLConnection(String urlString, String authToken,
                                                          String requestMethod, boolean doOutput)
            throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod(requestMethod);
        connection.setDoOutput(doOutput);

        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");

        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }

        connection.connect();
        return connection;
    }

    public String doPut(String urlString, String body, String authToken) throws IOException, IOException {
        HttpURLConnection connection = getHttpURLConnection(urlString, authToken, "PUT", true);
        sendRequest(connection, body);
        return getResponseBody(connection);
    }

    public String doDelete(String urlString, String authToken) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(urlString, authToken, "DELETE", true);
        return getResponseBody(connection);
    }

    public String doGet(String urlString, String authToken) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(urlString, authToken, "GET", false);
        return getResponseBody(connection);
    }

    private String getResponseBody(HttpURLConnection connection) throws IOException {
        InputStream stream = connection.getResponseCode() == HttpURLConnection.HTTP_OK
                ? connection.getInputStream()
                : connection.getErrorStream();

        if (stream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, "utf-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
        }

        // error checks
        if (response.toString().contains("already taken")) {
            System.err.println("Already Taken");
            System.out.println();
        }
        if (response.toString().contains("unauthorized")) {
            System.err.println("Unauthorized");
            System.out.println();
        }

        return response.toString();
    }


    private void sendRequest(HttpURLConnection connection, String body){
        try (OutputStream requestBody = connection.getOutputStream();) {
            byte[] input = body.getBytes("utf-8");
            requestBody.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
