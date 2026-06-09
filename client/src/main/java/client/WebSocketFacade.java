package client;

import com.google.gson.Gson;
import jakarta.websocket.Endpoint;
import jakarta.websocket.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();

    public WebSocketFacade(String serverUrl) throws Exception {

        serverUrl = serverUrl.replace("http", "ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        this.session = container.connectToServer(this, URI.create(serverUrl + "/ws"));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

        session.addMessageHandler(String.class, message -> {

            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

            System.out.println("Received: " + serverMessage.getServerMessageType());
        });
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }
}