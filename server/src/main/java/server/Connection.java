package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.ConnectCommand;

public class Connection {
    private final Session session;
    private final String username;
    private final ConnectCommand.Role role;

    public Connection(Session session, String username, ConnectCommand.Role role) {
        this.session = session;
        this.username = username;
        this.role = role;
    }

}


