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
        return "Board updated!";// this changes the board i think - double check that tho. then update database with the mutated board
    }

    private String connect(UserGameCommand command) {
        return "not implemented";
    }

    private String leave(UserGameCommand command) {
        return "not implemented";
    }

    private String resign(UserGameCommand command) {
        return "not implemented";
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
