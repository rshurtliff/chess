package ui;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                var type = serverMessage.getServerMessageType();
                switch (type) {
                    case LOAD_GAME -> serverMessageObserver.notify(new Gson().fromJson(message, LoadGameMessage.class));
                    case NOTIFICATION ->
                            serverMessageObserver.notify(new Gson().fromJson(message, NotificationMessage.class));
                    case ERROR -> serverMessageObserver.notify(new Gson().fromJson(message, ErrorMessage.class));
                }
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
