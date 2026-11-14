package ui;

import jakarta.websocket.*;

import java.net.URI;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {

    //main can be deleted, just for testing
    public static void main(String[] args) throws Exception {
        var ws = new WebsocketCommunicator(8080);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a message to echo");
        while (true) {
            ws.send(scanner.nextLine());
        }
    }

    public Session session;

    public WebsocketCommunicator(int port) throws Exception {
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
