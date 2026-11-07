package ui;

import client.ResponseException;
import client.ServerFacade;
import org.glassfish.grizzly.http.server.Response;

import java.util.Locale;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Repl {
    private State state;
    private ServerFacade serverFacade;

    public Repl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        state = State.SIGNED_OUT;
    }

    public enum State {
        SIGNED_IN, SIGNED_OUT, QUIT
    }

    public void run() {
        System.out.println("Welcome to chess for CS 240. Type " + SET_TEXT_COLOR_RED + "help" +
                RESET_TEXT_COLOR + " to get started");
        Scanner scanner = new Scanner(System.in);

        while (!state.equals(State.QUIT)) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();

            if (state == State.SIGNED_OUT) {
                handePreLogin(input);
            } else {
                handlePostLogin(input);
            }
        }
        scanner.close();
    }

    private void handePreLogin(String input) {
        String[] arguments = input.trim().split("\\s+");
        if (!validateArguments(arguments)) {
            System.out.println("Error: invalid command");
            help();
            return;
        }
        String cmd = arguments[0];
        switch (cmd.toLowerCase()) {
            case "help" -> help();
            case "register" -> registerHandler(arguments);
            case "login" -> loginHandler(arguments);
            case "exit" -> state = State.QUIT;
        }
    }

    private void handlePostLogin(String input) {
        //post login here
        String[] arguments = input.trim().split("\\s+");
        if (!validateArguments(arguments)) {
            System.out.println("Error: invalid command");
            help();
            return;
        }
        String cmd = arguments[0];
        switch (cmd.toLowerCase()) {
            case "help" -> help();
            case "list" -> listHandler();
            case "join" -> joinHandler(arguments);
            case "observe" -> observerHandler(arguments);
            case "logout" -> logoutHandler();
            case "quit" -> state = State.QUIT;
        }

    }

    private void logoutHandler() {
        try {
            serverFacade.logout();
            System.out.println("Successfully logged out!");
            state = State.SIGNED_OUT;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    private void observerHandler(String[] arguments) {
    }

    private void joinHandler(String[] arguments) {
    }

    private void listHandler() {
    }

    private void registerHandler(String[] arguments) {
        try {
            serverFacade.register(arguments[1], arguments[2], arguments[3]);
            System.out.println("Registration Successful!");
            state = State.SIGNED_IN;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loginHandler(String[] arguments) {
        try {
            serverFacade.login(arguments[1], arguments[2]);
            System.out.println("Successfully Logged In!");
            state = State.SIGNED_IN;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    private void help() {
        if (state.equals(State.SIGNED_OUT)) {
            System.out.println(SET_TEXT_COLOR_RED + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("exit - to stop the program");
            System.out.println("help - with possible commands\n" + RESET_TEXT_COLOR);
        } else {
            System.out.print(SET_TEXT_COLOR_RED +
                    """
                            create <NAME> - a game
                            list - games
                            join <ID> - a game
                            logout - when you are done
                            quit - playing chess
                            help - with possible commands
                            """
                    + RESET_TEXT_COLOR);
        }
    }

    private boolean validateArguments(String[] arguments) {
        if (arguments.length == 0) {
            return false;
        }
        String cmd = arguments[0];
        if (state == State.SIGNED_OUT) {
            if (cmd.equalsIgnoreCase("register")) {
                return arguments.length == 4;
            }
            if (cmd.equalsIgnoreCase("login")) {
                return arguments.length == 3;
            }
        } else if (state == State.SIGNED_IN) {
            if (cmd.equalsIgnoreCase("create")) {
                return arguments.length == 2;
            }
            if (cmd.equalsIgnoreCase("join")) {
                return arguments.length == 3;
            }
            if (cmd.equalsIgnoreCase("observe")) {
                return arguments.length == 2;
            }
            if (cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("logout")) {
                return arguments.length == 1;
            }
        }
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit")) {
            return arguments.length == 1;
        }
        return false;
    }
}

