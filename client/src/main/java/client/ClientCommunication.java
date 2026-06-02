package client;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;

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

            connection.setRequestProperty(method);



        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            throw new ClientException("Unable to talk to the server on MySQL ; sorry");
        }
    }
}
