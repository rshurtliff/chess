package ui;

import client.ServerFacade;

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
        System.out.println("Welcome to chess for CS 240. Type " + SET_TEXT_COLOR_RED + "help" + RESET_TEXT_COLOR + " to get started");
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
            case "register" -> serverFacade.register(arguments[1], arguments[2], arguments[3]);
            case "login" -> System.out.println("login(arguments)");
            case "exit" -> state = State.QUIT;
        }
    }

    private void handlePostLogin(String input) {
        //post login here
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
        if (cmd.equalsIgnoreCase("register")) {
            return arguments.length == 4;
        }
        if (cmd.equalsIgnoreCase("login")) {
            return arguments.length == 3;
        }
        if (cmd.equalsIgnoreCase("create")) {
            return arguments.length == 2;
        }
        if (cmd.equalsIgnoreCase("join")) {
            return arguments.length == 3;
        }
        if (cmd.equalsIgnoreCase("observe")) {
            return arguments.length == 2;
        }
        return cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit") ||
                cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("logout");
    }
}

