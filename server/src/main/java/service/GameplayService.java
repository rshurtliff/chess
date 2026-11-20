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
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;


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
            case UserGameCommand.CommandType.MAKE_MOVE -> makeMove((MakeMoveCommand) command);
            case UserGameCommand.CommandType.CONNECT -> connect((ConnectCommand) command, session);
            case UserGameCommand.CommandType.LEAVE -> leave(command);
            case UserGameCommand.CommandType.RESIGN -> resign(command);
        }
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION); // this will need to be changed to void function later
    }

    private ServerMessage makeMove(MakeMoveCommand command) {
        //have this send the message back to the WebsocketCommunicator, not return it
        try {
            GameData gameData = dataAccess.getGame(command.getGameID());
            ChessMove proposedMove = command.getMove();
            gameData.game().makeMove(proposedMove);
            dataAccess.updateGame(gameData);
            return new LoadGameMessage(dataAccess.getGame(command.getGameID()));
        } catch (InvalidMoveException e) {
            return new ErrorMessage("Error: Invalid move! (Hint - make sure it's your turn!)");
        } catch (DataAccessException e) {
            return new ErrorMessage("Error: " + e.getMessage());
        }
    }

    private void connect(ConnectCommand command, Session root) throws Exception {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        connectionManager.add(command.getGameID(), new Connection(root, username, command.getRole()));
        String rootMessage = new Gson().toJson(new LoadGameMessage(dataAccess.getGame(command.getGameID())));
        root.getRemote().sendString(rootMessage);

        //notify everyone else

        //send a LOAD_GAME TO root, send NOTIFY to everyone else
    }

    private ServerMessage leave(UserGameCommand command) {
        return null;
    }

    private ServerMessage resign(UserGameCommand command) {
        return null;
    }

    private UserGameCommand parseCommandFromJson(String json) {
        UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);
        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            command = new Gson().fromJson(json, MakeMoveCommand.class);
        }
        return command;
    }

    private void authenticate(UserGameCommand command) throws Exception {
        var auth = dataAccess.getAuth(command.getAuthToken());
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

}
