package ui;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ObserverClient implements Client {
    private final GamePlayClient client;
    private Repl.State nextState;

    public ObserverClient(GamePlayClient client) {
        this.client = client;
        nextState = Repl.State.OBSERVING;
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_RED +
                """ 
                        redraw - the chess board
                        leave - the game
                        highlight <PIECE_POSITION> - highlight legal moves for a piece
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
        nextState = Repl.State.OBSERVING;
        String[] arguments = input.trim().split("\\s+");
        if (!client.validateArguments(arguments)) {
            help();
            return ("Error: invalid command");
        }
        String cmd = arguments[0];
        return switch (cmd.toLowerCase()) {
            case "redraw" -> client.redraw();
            case "leave" -> leave();
            case "highlight" -> client.highlight(arguments);
            case "help" -> help();
            default -> "Unknown Command";
        };

    }

    private String leave() {
        nextState = Repl.State.SIGNED_IN;
        return client.leave();
    }

}
