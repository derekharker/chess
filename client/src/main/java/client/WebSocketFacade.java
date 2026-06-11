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
import jakarta.websocket.CloseReason;

import java.nio.ByteBuffer;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();
    private final Consumer<ServerMessage> messageHandler;

    private final ScheduledExecutorService heartbeat =
            Executors.newSingleThreadScheduledExecutor();

    public WebSocketFacade(String serverUrl, Consumer<ServerMessage> messageHandler) throws Exception {
        this.messageHandler = messageHandler;
        serverUrl = serverUrl.replace("http", "ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxSessionIdleTimeout(0);
        jakarta.websocket.ClientEndpointConfig config = jakarta.websocket.ClientEndpointConfig.Builder.create().build();
        this.session = container.connectToServer(this, config, URI.create(serverUrl + "/ws"));

        heartbeat.scheduleAtFixedRate(() -> {
            try {
                if (session != null && session.isOpen()) {
                    session.getBasicRemote().sendPing(ByteBuffer.wrap(new byte[]{1}));
                }
            } catch (Throwable e) {
                System.out.println("[heartbeat failed] " + e.getClass().getName() + ": " + e.getMessage());
            }
        }, 5, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;

        session.setMaxIdleTimeout(0); //testing

        session.addMessageHandler(String.class, message -> {
//got rid of debug print
            JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
            String type = obj.get("serverMessageType").getAsString();

            ServerMessage parsed = switch (type) {
                case "LOAD_GAME" -> gson.fromJson(message, LoadGameMessage.class);
                case "NOTIFICATION" -> gson.fromJson(message, NotificationMessage.class);
                case "ERROR" -> gson.fromJson(message, ErrorMessage.class);
                default -> gson.fromJson(message, ServerMessage.class);
            };

            messageHandler.accept(parsed);
        });
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() throws Exception {
        heartbeat.shutdownNow();

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }

    public boolean isOpen() {
        return session != null && session.isOpen();
    }
}