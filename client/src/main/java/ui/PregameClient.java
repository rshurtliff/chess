package ui;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

import client.ResponseException;
import client.ServerFacade;
import ui.Repl.State;

public class PregameClient implements Client {
    private final ServerFacade serverFacade;
    private State nextState;

    public PregameClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.nextState = State.SIGNED_OUT;
    }

    @Override
    public String help() {
        return (SET_TEXT_COLOR_RED +
                """
                        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                        login <USERNAME> <PASSWORD> - to play chess
                        exit - to stop the program
                        help - with possible commands
                        
                        """
                + RESET_TEXT_COLOR);
    }

    @Override
    public Repl.State nextState() {
        return nextState;
    }


    @Override
    public String eval(String input) {
        String[] arguments = input.trim().split("\\s+");
        if (!validateArguments(arguments)) {
            return ("Error: invalid command\n" + help());
        }
        String cmd = arguments[0];
        return switch (cmd.toLowerCase()) {
            case "help" -> help();
            case "register" -> registerHandler(arguments);
            case "login" -> loginHandler(arguments);
            case "exit" -> exitHandler();
            default -> "Unknown Command!";
        };
    }


    private boolean validateArguments(String[] arguments) {
        if (arguments.length == 0) {
            return false;
        }
        String cmd = arguments[0];
        if (cmd.equalsIgnoreCase("register")) {
            return arguments.length == 4;
        }
        if (cmd.equalsIgnoreCase("login")) {
            return arguments.length == 3;
        }
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit")) {
            return arguments.length == 1;
        }
        return false;
    }

    private String registerHandler(String[] arguments) {
        try {
            serverFacade.register(arguments[1], arguments[2], arguments[3]);
            nextState = Repl.State.SIGNED_IN;
            return ("Registration Successful!");
        } catch (ResponseException e) {
            return (e.getMessage());
        }
    }

    private String loginHandler(String[] arguments) {
        try {
            serverFacade.login(arguments[1], arguments[2]);
            nextState = State.SIGNED_IN;
            return ("Successfully Logged In!");
        } catch (ResponseException e) {
            return (e.getMessage());
        }
    }

    private String exitHandler() {
        nextState = State.QUIT;
        return ("Goodbye!");
    }
}
