package ui;

import chess.*;
import client.ServerFacade;
import com.google.gson.Gson;
import jakarta.websocket.WebSocketContainer;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;

import java.util.Set;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class GamePlayClient implements Client, ServerMessageObserver {
    private final ServerFacade serverFacade;
    private Repl.State nextState;
    private GameplayData currentGame;

    public GamePlayClient(ServerFacade serverFacade, GameplayData currentGame) {
        this.serverFacade = serverFacade;
        nextState = Repl.State.PLAYING_GAME;
        this.currentGame = currentGame;

    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_RED +
                """ 
                        redraw - the chess board
                        leave - the game
                        move <START_POSITION> <END_POSITION> [Promotion_Piece] - i.e. "move A2 A3" or "move A7 A8 queen"
                        resign - forfeit and end game
                        highlight <PIECE_POSITION> - highlight legal moves for a piece
                        exit - playing chess
                        help - with possible commands
                        
                        """
                + RESET_TEXT_COLOR;
    }

    @Override
    public Repl.State nextState() {
        return nextState;
    }

    @Override
    public String eval(String input) {
        String[] arguments = input.trim().split("\\s+");
        if (!validateArguments(arguments)) {
            help();
            return ("Error: invalid command");
        }
        String cmd = arguments[0];
        return switch (cmd.toLowerCase()) {
            case "move" -> moveHandler(arguments);
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "resign" -> resign();
            case "highlight" -> highlight(arguments);
            case "help" -> help();
            default -> "Unknown Command";
        };
    }

    private String moveHandler(String[] arguments) {
        ChessPiece.PieceType promotionType = arguments.length == 4 ? ChessPiece.PieceType.valueOf(arguments[3].toUpperCase()) : null;
        ChessMove proposedMove = new ChessMove(convertToPosition(arguments[1]), convertToPosition(arguments[2]), promotionType);
        int gameId = currentGame.gameData().gameID();
        try {
            serverFacade.makeMove(gameId, proposedMove);
            return "NOT SURE WHAT TO RETURN HERE, PROBABLY THE NEW BOARD AFTER THE MOVE RENDERED";
        } catch (Exception e) {
            return ("Error: " + e.getMessage());
        }
    }

    private String redraw() {
        if (currentGame.pointOfView().equals(ChessGame.TeamColor.WHITE)) {
            return RenderBoard.showBoardForWhite(currentGame.gameData().game().getBoard(), null);
        } else {
            return RenderBoard.showBoardForBlack(currentGame.gameData().game().getBoard(), null);
        }
    }

    private String leave() {
        //this will be a websocket command too
        nextState = Repl.State.SIGNED_IN;
        return "NOT IMPLEMENTED";
    }

    private String resign() {
        //make sure they really wanna resign - websocket
        return "NOT IMPLEMENTED";
    }

    private String highlight(String[] arguments) {
        var position = convertToPosition(arguments[1]);
        ChessBoard board = currentGame.gameData().game().getBoard();
        if (board.getPiece(position) == null) {
            return "Error: no piece at specified position!";
        }
        var validMoves = currentGame.gameData().game().validMoves(position);
        if (currentGame.pointOfView().equals(ChessGame.TeamColor.WHITE)) {
            return RenderBoard.showBoardForWhite(board, validMoves);
        } else {
            return RenderBoard.showBoardForBlack(board, validMoves);
        }
    }

    private ChessPosition convertToPosition(String stringPos) {
        int col = Character.toLowerCase(stringPos.charAt(0)) - 'a' + 1;
        int row = Character.toLowerCase(stringPos.charAt(1)) - '0';
        return new ChessPosition(row, col);
    }

    private boolean validateArguments(String[] arguments) {
        if (arguments.length == 0) {
            return false;
        }
        String cmd = arguments[0];
        if (cmd.equalsIgnoreCase("redraw")) {
            return arguments.length == 1;
        }
        if (cmd.equalsIgnoreCase("leave")) {
            return arguments.length == 1;
        }
        if (cmd.equalsIgnoreCase("move")) {
            if (arguments.length == 3) {
                boolean isValid = validatePosition(arguments[1]) && validatePosition(arguments[2]);
                if (!isValid) {
                    System.out.println("Make sure moves are <START_POS> <END_POS> [PROMOTION]. " +
                            "Only specify promotion for pawns moving to the last row. Ex: move a2 a3");
                }
                return isValid;
            } else if (arguments.length == 4) {
                boolean validPositions = validatePosition(arguments[1]) && validatePosition(arguments[2]);
                String promotionPiece = arguments[3].trim().toLowerCase();
                Set<String> validPromotions = Set.of("queen", "bishop", "rook", "knight");
                boolean isValid = validPositions && validPromotions.contains(promotionPiece);
                if (!isValid) {
                    System.out.println("Make sure moves are <START_POS> <END_POS> [PROMOTION]. " +
                            "Only specify promotion for pawns moving to the last row. Ex: move a2 a3");
                }
                return isValid;
            }
            return false;
        }
        if (cmd.equalsIgnoreCase("resign")) {
            return arguments.length == 1;
        }
        if (cmd.equalsIgnoreCase("highlight")) {
            //gotta make sure that the requested position is valid
            return arguments.length == 2;
        }
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit")) {
            return arguments.length == 1;
        }
        return false;
    }

    private boolean validatePosition(String posString) {
        if (posString.length() != 2) {
            return false;
        }
        char col = Character.toLowerCase(posString.charAt(0));
        char row = posString.charAt(1);
        return (col >= 'a' && col <= 'h' && row >= '1' && row <= '8');
    }

    public GameplayData getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameplayData gameData) {
        currentGame = gameData;
    }

    public void connectToWebsocket() {
        try {
            serverFacade.connect(this, currentGame.gameData().gameID());
        } catch (Exception e) {
            System.out.println("Error: Couldn't connect to websocket");
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {

    }
}
