package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
        authenticate(command, session);
        switch (command.getCommandType()) {
            case UserGameCommand.CommandType.MAKE_MOVE -> makeMove((MakeMoveCommand) command, session);
            case UserGameCommand.CommandType.CONNECT -> connect((ConnectCommand) command, session);
            case UserGameCommand.CommandType.LEAVE -> leave(command, session);
            case UserGameCommand.CommandType.RESIGN -> resign(command, session);
        }
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION); // this will need to be changed to void function later
    }

    private void makeMove(MakeMoveCommand command, Session root) throws IOException, DataAccessException {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        if (dataAccess.getGame(command.getGameID()) == null) {
            connectionManager.sendError("Error: Tried to connect to a GameID that doesn't exist!", root);
            return;
        }
        try {
            int gameID = command.getGameID();
            GameData gameData = dataAccess.getGame(gameID);
            // need to make sure you can't move for your opponent.
            ChessGame.TeamColor pieceOwnerColor = gameData.game().getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor();
            String pieceOwnerUsername = (pieceOwnerColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            if (!pieceOwnerUsername.equals(username)) {
                connectionManager.sendError("Error: You can't move that piece, it's not yours!", root);
                return;
            }

            if (gameData.game().getStatus() == ChessGame.Status.OVER) {
                connectionManager.sendError("Error: Game is over. No moves can be made", root);
                return;
            }
            ChessMove proposedMove = command.getMove();

            gameData.game().makeMove(proposedMove);
            dataAccess.updateGame(gameData);

            GameData updatedGame = dataAccess.getGame(gameID);

            connectionManager.notifyAll(gameID, new LoadGameMessage(updatedGame));

            connectionManager.notifyNonRoot(gameID, new NotificationMessage(username + " moved " + prettifyPosition(proposedMove.getStartPosition())
                    + " to " + prettifyPosition(proposedMove.getEndPosition())), root);
            checkGameState(updatedGame);
        } catch (InvalidMoveException e) {
            var error = new ErrorMessage("Error: invalid move! Make sure it's your turn!");
            root.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private String prettifyPosition(ChessPosition pos) {
        char col = (char) ('a' + pos.getColumn() - 1);
        char row = (char) ('0' + pos.getRow());
        return ("" + col + row);
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


    private void connect(ConnectCommand command, Session root) throws DataAccessException, IOException {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        if (dataAccess.getGame(command.getGameID()) == null) {
            connectionManager.sendError("Error: Tried to connect to a GameID that doesn't exist!", root);
            return;
        }
        connectionManager.add(command.getGameID(), new Connection(root, username, command.getRole()));
        String rootMessage = new Gson().toJson(new LoadGameMessage(dataAccess.getGame(command.getGameID())));
        root.getRemote().sendString(rootMessage);

        int gameID = command.getGameID();
        ServerMessage nonRootMessage = new NotificationMessage(username + " has joined as " + command.getRole());
        connectionManager.notifyNonRoot(gameID, nonRootMessage, root);
    }

    private void leave(UserGameCommand command, Session root) throws DataAccessException {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        int gameID = command.getGameID();
        GameData oldGame = dataAccess.getGame(gameID);
        Connection connection = connectionManager.getConnection(gameID, root);
        ConnectCommand.Role role = connection.getRole();
        //ghetto test version
        if (role == null) {
            if (oldGame.whiteUsername() != null && oldGame.whiteUsername().equals(username)) {
                role = ConnectCommand.Role.WHITE;
            } else if (oldGame.blackUsername() != null && oldGame.blackUsername().equals(username)) {
                role = ConnectCommand.Role.BLACK;
            } else {
                role = ConnectCommand.Role.OBSERVER;
            }
        }

        if (role == ConnectCommand.Role.BLACK) {
            GameData newGame = new GameData(gameID, oldGame.whiteUsername(), null, oldGame.gameName(), oldGame.game());
            dataAccess.updateGame(newGame);
        } else if (role == ConnectCommand.Role.WHITE) {
            GameData newGame = new GameData(gameID, null, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            dataAccess.updateGame(newGame);
        }

        removeConnection(root);

        ServerMessage message = new NotificationMessage(username + " has left the game as " + role);
        connectionManager.notifyNonRoot(gameID, message, root);
    }

    private void resign(UserGameCommand command, Session session) throws DataAccessException, IOException {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        GameData game = dataAccess.getGame(command.getGameID());
        if (!(username.equals(game.whiteUsername()) || username.equals(game.blackUsername()))) {
            connectionManager.sendError("You're not a player, you can't resign!", session);
            return;
        }
        if (game.game().getStatus().equals(ChessGame.Status.OVER)) {
            connectionManager.sendError("The game is already over!", session);
            return;
        }
        game.game().endGame();
        dataAccess.updateGame(game);
        connectionManager.notifyAll(command.getGameID(), new NotificationMessage(username + " has resigned. Game over!"));
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

    private void authenticate(UserGameCommand command, Session root) {
        try {
            var auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                connectionManager.sendError("Error: You're not authorized!", root);
            }
        } catch (DataAccessException e) {
            connectionManager.sendError("Error: You're not authorized!", root);
        }
    }

    public void removeConnection(Session session) {
        connectionManager.remove(session);
    }
}
