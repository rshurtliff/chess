package server;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;


import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private ConcurrentHashMap<Integer, Set<Connection>> connections;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
    }

    public void add(Integer gameID, Connection connection) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(connection);
    }

    public void notifyNonRoot(Integer gameID, ServerMessage message, Session session) throws IOException {
        for (Connection connection : connections.get(gameID)) {
            if (!connection.getSession().equals(session)) {
                String notification = new Gson().toJson(message);
                connection.getSession().getRemote().sendString(notification);
            }
        }
    }

    public void notifyAll(Integer gameID, ServerMessage message) throws IOException {
        for (Connection connection : connections.get(gameID)) {
            String notification = new Gson().toJson(message);
            connection.getSession().getRemote().sendString(notification);
        }
    }

    public void remove(Session session) {
        for (Set<Connection> connectionSet : connections.values()) {
            connectionSet.removeIf(connection -> connection.getSession().equals(session));
        }
    }
}
