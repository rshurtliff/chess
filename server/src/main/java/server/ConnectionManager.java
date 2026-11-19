package server;

import websocket.messages.ServerMessage;

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

    public void notifyNonRoot(Integer gameID, ServerMessage message) {

    }
}
