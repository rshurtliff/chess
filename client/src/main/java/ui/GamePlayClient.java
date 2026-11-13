package ui;

import client.ServerFacade;

import java.util.Set;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class GamePlayClient implements Client {
    private final ServerFacade serverFacade;
    private Repl.State nextState;

    public GamePlayClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        nextState = Repl.State.PLAYING_GAME;
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
            case "help" -> help();
            default -> "Unknown Command";
        };
    }

    private String moveHandler(String[] arguments) {
        //do something here - this'll be websocket - you probably need to read the spec again.
        return "NOT IMPLEMENTED";
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
            if (arguments.length == 3 && arguments[1].length() == 2 && arguments[2].length() == 2) {
                char startCol = Character.toLowerCase(arguments[1].charAt(0));
                char startRow = arguments[1].charAt(1);
                char endCol = Character.toLowerCase(arguments[2].charAt(0));
                char endRow = arguments[2].charAt(1);
                return (startCol >= 'a' && startCol <= 'h' && endCol >= 'a' && endCol <= 'h'
                        && startRow >= '1' && startRow <= '8' && endRow >= '1' && endRow <= '8');
            } else if (arguments.length == 4 && arguments[1].length() == 2 && arguments[2].length() == 2) {
                char startCol = Character.toLowerCase(arguments[1].charAt(0));
                char startRow = arguments[1].charAt(1);
                char endCol = Character.toLowerCase(arguments[2].charAt(0));
                char endRow = arguments[2].charAt(1);
                String promotionPiece = arguments[3].trim().toLowerCase();
                Set<String> validPromotions = Set.of("queen", "bishop", "rook", "knight");
                return (startCol >= 'a' && startCol <= 'h' && endCol >= 'a' && endCol <= 'h'
                        && startRow >= '1' && startRow <= '8' && endRow >= '1' && endRow <= '8' && validPromotions.contains(promotionPiece));
            }
            return false;
        }
        if (cmd.equalsIgnoreCase("resign")) {
            return arguments.length == 1;
        }
        if (cmd.equalsIgnoreCase("highlight")) {
            return arguments.length == 2;
        }
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit")) {
            return arguments.length == 1;
        }
        return false;
    }
}
