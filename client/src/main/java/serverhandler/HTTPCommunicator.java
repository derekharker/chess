package serverhandler;

import java.io.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

public class HTTPCommunicator {

    public String doPost(String url, String body, String authToken) throws IOException {
        HttpURLConnection conn =  getHttpURLConnection(url, authToken, "POST", true);
        sendRequest(conn, body);
        return getResponseBody(conn);
    }

    public String doPut(String url, String body, String authToken) throws IOException {
        HttpURLConnection conn =  getHttpURLConnection(url, authToken, "PUT", true);
        sendRequest(conn, body);
        return getResponseBody(conn);
    }

    public String doDelete(String url, String authToken) throws IOException {
        HttpURLConnection conn =  getHttpURLConnection(url, authToken, "DELETE", true);
        return getResponseBody(conn);
    }

    public String doGet(String url, String authToken) throws IOException {
        HttpURLConnection conn =  getHttpURLConnection(url, authToken, "GET", false); //Maybe change this ...?
        return getResponseBody(conn);
    }

    private String getResponseBody(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream responseBody = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(
                         responseBody, "utf-8"))) {

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line.trim());
                    }
                return response.toString();
            }
        } else try (InputStream responseBody = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            responseBody, "utf-8")))    {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        }
    }

    private void sendRequest(HttpURLConnection conn, String body) {
        try (OutputStream stream = conn.getOutputStream();) {
            byte[] input = body.getBytes("utf-8");
            stream.write(input, 0, input.length);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static HttpURLConnection getHttpURLConnection(String url, String authToken, String reqMethod, boolean out)
            throws IOException {
        URL newUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();

        conn.setReadTimeout(5000);
        conn.setRequestMethod(reqMethod);
        conn.setDoOutput(out);

        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");

        // Add an authToken if valid
        if (authToken != null) {
            conn.addRequestProperty("Authorization", authToken);
        }

        conn.connect();
        return conn;
    }


}
