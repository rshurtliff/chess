package service;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import server.Connection;
import server.ConnectionManager;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;


public class GameplayService {
    private final DataAccess dataAccess;
    private final ConnectionManager connectionManager;

    public GameplayService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        connectionManager = new ConnectionManager();
    }

    public ServerMessage executeUserGameCommand(String json, Session session) throws Exception {
        UserGameCommand command = parseCommandFromJson(json);
        authenticate(command);
        switch (command.getCommandType()) {
            case UserGameCommand.CommandType.MAKE_MOVE -> makeMove((MakeMoveCommand) command, session);
            case UserGameCommand.CommandType.CONNECT -> connect((ConnectCommand) command, session);
            case UserGameCommand.CommandType.LEAVE -> leave(command, session);
            case UserGameCommand.CommandType.RESIGN -> resign(command);
        }
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION); // this will need to be changed to void function later
    }

    private void makeMove(MakeMoveCommand command, Session root) throws IOException, DataAccessException {
        //have this send the message back to the WebsocketCommunicator, not return it
        try {
            int gameID = command.getGameID();
            GameData gameData = dataAccess.getGame(gameID);
            ChessMove proposedMove = command.getMove();

            var moveColor = gameData.game().getTeamTurn();
            gameData.game().makeMove(proposedMove);
            dataAccess.updateGame(gameData);

            GameData updatedGame = dataAccess.getGame(gameID);

            connectionManager.notifyAll(gameID, new LoadGameMessage(updatedGame));
            connectionManager.notifyNonRoot(gameID, new NotificationMessage(moveColor + " moved " + proposedMove.getStartPosition()
                    + " to " + proposedMove.getEndPosition()), root);
            checkGameState(updatedGame);
        } catch (InvalidMoveException e) {
            var error = new ErrorMessage("Error: invalid move! Make sure it's your turn!");
            root.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void checkGameState(GameData updatedGame) throws IOException {
        var colorToCheck = updatedGame.game().getTeamTurn();
        int gameID = updatedGame.gameID();
        boolean staleMate = updatedGame.game().isInStalemate(colorToCheck);
        boolean checkmate = updatedGame.game().isInCheckmate(colorToCheck);
        boolean check = updatedGame.game().isInCheck(colorToCheck);
        NotificationMessage message = null;
        if (staleMate) {
            message = new NotificationMessage(colorToCheck + " is in stalemate! Game over.");
        } else if (checkmate) {
            message = new NotificationMessage(colorToCheck + " is in checkmate! Game over.");
        } else if (check) {
            message = new NotificationMessage(colorToCheck + " is in check!");
        }
        if (message != null) {
            connectionManager.notifyAll(gameID, message);
        }
    }


    private void connect(ConnectCommand command, Session root) throws Exception {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        connectionManager.add(command.getGameID(), new Connection(root, username, command.getRole()));
        String rootMessage = new Gson().toJson(new LoadGameMessage(dataAccess.getGame(command.getGameID())));
        root.getRemote().sendString(rootMessage);

        //notify everyone else
        int gameID = command.getGameID();
        ServerMessage nonRootMessage = new NotificationMessage(username + " has joined as " + command.getRole());
        connectionManager.notifyNonRoot(gameID, nonRootMessage, root);

        //send a LOAD_GAME TO root, send NOTIFY to everyone else
    }

    private void leave(UserGameCommand command, Session root) throws DataAccessException {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        int gameID = command.getGameID();
        GameData oldGame = dataAccess.getGame(gameID);
        Connection connection = connectionManager.getConnection(gameID, root);
        ConnectCommand.Role role = connection.getRole();
        if (role == ConnectCommand.Role.BLACK_PLAYER) {
            GameData newGame = new GameData(gameID, oldGame.whiteUsername(), null, oldGame.gameName(), oldGame.game());
            dataAccess.updateGame(newGame);
        } else if (role == ConnectCommand.Role.WHITE_PLAYER) {
            GameData newGame = new GameData(gameID, null, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            dataAccess.updateGame(newGame);
        }

        removeConnection(root);
        connectionManager.notifyRoot("You left the game", root);

        ServerMessage message = new NotificationMessage(username + " has left the game as " + role);
        connectionManager.notifyNonRoot(gameID, message, root);
    }

    private void resign(UserGameCommand command) {
    }

    private UserGameCommand parseCommandFromJson(String json) {
        UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);
        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            command = new Gson().fromJson(json, MakeMoveCommand.class);
        } else if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            command = new Gson().fromJson(json, ConnectCommand.class);
        }
        return command;
    }

    private void authenticate(UserGameCommand command) throws Exception {
        var auth = dataAccess.getAuth(command.getAuthToken());
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public void removeConnection(Session session) {
        connectionManager.remove(session);
    }
}
