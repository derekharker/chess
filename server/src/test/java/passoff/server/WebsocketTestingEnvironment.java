package passoff.server;

import com.google.gson.GsonBuilder;

import java.net.URISyntaxException;

public class WebsocketTestingEnvironment extends passoff.websocket.WebsocketTestingEnvironment {

    public WebsocketTestingEnvironment(String host, String port, String path, GsonBuilder gsonBuilder) throws URISyntaxException {
        super(host, port, path, gsonBuilder);
    }
}