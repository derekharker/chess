package serverhandler;

import jakarta.websocket.*;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import translatorclient.ClientTranslation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import jakarta.websocket.EndpointConfig;

public class WSCommunicator extends Endpoint {
    String url;
    Session session;
    ServerMessageObserver observer;

    public WSCommunicator(String url, ServerMessageObserver observer) {
        this.url = url.replace("http", "ws");

        this.observer = observer;

        try {
            initWebSocket();
        } catch (URISyntaxException | DeploymentException | IOException ex) {
            System.out.println("Error connecting to websocket. In WS Communicator: " + ex.getMessage());
        }
    }

    private void initWebSocket() throws URISyntaxException, DeploymentException, IOException {
        URI socketURI = new URI(this.url + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleIncomingMessage(message);
            }
        });
    }

    private void handleIncomingMessage(String message) {
        try {
            ServerMessage base =
                    ClientTranslation.fromJsontoObjectNotRequest(message, ServerMessage.class);

            ServerMessage fullMessage = createTypedMessage(base, message);
            if (fullMessage == null) {
                // Unknown type already logged in createTypedMessage
                return;
            }

            observer.notify(fullMessage);

        } catch (Exception ex) {
            System.out.println("Error handling WS message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private ServerMessage createTypedMessage(ServerMessage base, String rawJson) {
        switch (base.getServerMessageType()) {
            case LOAD_GAME:
                return ClientTranslation.fromJsontoObjectNotRequest(rawJson, LoadGameMessage.class);
            case ERROR:
                return ClientTranslation.fromJsontoObjectNotRequest(rawJson, ErrorMessage.class);
            case NOTIFICATION:
                return ClientTranslation.fromJsontoObjectNotRequest(rawJson, NotificationMessage.class);
            default:
                System.out.println("Unknown serverMessageType: " + base.getServerMessageType());
                return null;
        }
    }
    public void connect(ConnectCommand command) {
        try {
            assert this.session != null;
            this.session.getBasicRemote().sendText(ClientTranslation.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to connect: " + e.getMessage());
        }
    }

    public void makeMove(MakeMoveCommand command) {
        try {
            this.session.getBasicRemote().sendText(ClientTranslation.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to make move: " + e.getMessage());
        }
    }

    public void leave(LeaveGameCommand command) {
        try {
            this.session.getBasicRemote().sendText(ClientTranslation.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to leave: " + e.getMessage());
        }
    }

    public void resign(ResignCommand command) {
        try {
            this.session.getBasicRemote().sendText(ClientTranslation.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to resign: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
