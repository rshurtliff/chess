package server;

import com.google.gson.Gson;
import kotlin.TuplesKt;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
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

    public Connection getConnection(int gameID, Session session){
       Set<Connection> connectionSet = connections.get(gameID);
       for (Connection connection : connectionSet){
           if (connection.getSession().equals(session)){
               return connection;
           }
       }
       return null;
    }

    public void notifyRoot(String message, Session session) {
        try {
            ServerMessage notification = new NotificationMessage(message);
            session.getRemote().sendString(new Gson().toJson(notification));
        } catch (IOException e) {
            sendError(e.getMessage(), session);
        }
    }

    public void sendError(String message, Session session){
        ServerMessage error = new ErrorMessage(message);
        try {
            session.getRemote().sendString(new Gson().toJson(error));
        } catch (Exception e){
            System.out.println("COULDN'T CONNECT TO WEBSOCKET TO SEND ERROR");
        }
    }


    public void notifyNonRoot(Integer gameID, ServerMessage message, Session session) {
        try {
            for (Connection connection : connections.get(gameID)) {
                if (!connection.getSession().equals(session)) {
                    String notification = new Gson().toJson(message);
                    connection.getSession().getRemote().sendString(notification);
                }
            }
        } catch (Exception e) {
            sendError(e.getMessage(), session);
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
