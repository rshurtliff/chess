package server;

import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    private final Session session;
    private final String username;

    public Connection(Session session, String username) {
        this.session = session;
        this.username = username;
    }
}
