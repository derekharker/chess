package serverhandler;

import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WSCommunicator extends Endpoint {
    String url;
    Session session;
    ServerMessageObserver observer;

    public WSCommunicator(String url, ServerMessageObserver observer) {
        try {
            this.url = url.replace("http", "ws");
            URI socketURI = new URI(this.url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = TranslatorForClient.
                            fromJsontoObjectNotRequest(message, ServerMessage.class);
                    observer.notify(notification);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException ex) {
            System.out.println("Error connecting to websocket. In WS Communicator: " + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndPointConfig endPointConfig) {

    }
    public void connect(ConnectCommand command) {
        try {
            assert this.session != null;
            this.session.getBasicRemote().sendText(TranslatorForClient.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to connect: " + e.getMessage());
        }
    }

    public void makeMove(MakeMoveCommand command) {
        try {
            this.session.getBasicRemote().sendText(TranslatorForClient.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to make move: " + e.getMessage());
        }
    }

    public void leave(LeaveGameCommand command) {
        try {
            this.session.getBasicRemote().sendText(TranslatorForClient.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to leave: " + e.getMessage());
        }
    }

    public void resign(ResignCommand command) {
        try {
            this.session.getBasicRemote().sendText(TranslatorForClient.fromObjectToJson(command).toString());
        } catch (IOException e) {
            System.out.println("Unable to resign: " + e.getMessage());
        }
    }
}
