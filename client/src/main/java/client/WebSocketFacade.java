package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;
import java.util.function.Consumer;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();
    private final Consumer<ServerMessage> messageHandler;

    public WebSocketFacade(String serverUrl, Consumer<ServerMessage> messageHandler) throws Exception {
        this.messageHandler = messageHandler;
        serverUrl = serverUrl.replace("http", "ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, URI.create(serverUrl + "/ws"));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;

        session.addMessageHandler(String.class, message -> {
            JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
            String type = obj.get("serverMessageType").getAsString();

            ServerMessage parsed = switch (type) {
                case "LOAD_GAME" -> gson.fromJson(message, LoadGameMessage.class);
                case "NOTIFICATION" -> gson.fromJson(message, NotificationMessage.class);
                case "ERROR" -> gson.fromJson(message, ErrorMessage.class);//different messages for each not just server
                default -> gson.fromJson(message, ServerMessage.class);
            };

            messageHandler.accept(parsed);
        });
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}