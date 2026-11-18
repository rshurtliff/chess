package service;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import server.Server;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.awt.dnd.InvalidDnDOperationException;

public class GameplayService {
    private final DataAccess dataAccess;

    public GameplayService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;

    }

    public ServerMessage executeUserGameCommand(String json) throws Exception {
        UserGameCommand command = parseCommandFromJson(json);
        authenticate(command);
        return switch (command.getCommandType()) {
            case UserGameCommand.CommandType.MAKE_MOVE -> makeMove((MakeMoveCommand) command);
            case UserGameCommand.CommandType.CONNECT -> connect(command);
            case UserGameCommand.CommandType.LEAVE -> leave(command);
            case UserGameCommand.CommandType.RESIGN -> resign(command);
        };
    }

    private ServerMessage makeMove(MakeMoveCommand command) {
        //NOT SURE THESE ARE BEING SENT TO THE RIGHT PEOPLE --- NEED CONNECTION MANAGER
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

    private ServerMessage connect(UserGameCommand command) {
        return null;
        // should probably add them to a HashMap or something to track connections
        // then a ConnectionManager should probably notify the correct people based on what the command was.
        // notifications should just be ctx.send(JSON of a ServerMessage) - that's either ERROR, NOTIFICATION, or LOAD GAME sub class of ServerMessage
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
