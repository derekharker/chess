package serverhandler;

import java.io.*;
import java.io.IOException;
import java.net.HttpURLConnection;
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
                 BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody, "utf-8"))) {


            }
        }
    }
}
