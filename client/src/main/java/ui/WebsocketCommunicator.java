package ui;

import chess.ChessMove;
import chess.ChessPosition;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;

import java.net.URI;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {
    public Session session;
    private ServerMessageObserver serverMessageObserver;

    public WebsocketCommunicator(int port, ServerMessageObserver serverMessageObserver) throws Exception {
        this.serverMessageObserver = serverMessageObserver;
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
