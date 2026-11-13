package ui;

import client.ResponseException;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Repl {
    private State state;
    private final ServerFacade serverFacade;
    private final Client pregameClient;
    private final Client loggedinClient;

    public Repl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        pregameClient = new PregameClient(serverFacade);
        loggedinClient = new LoggedInClient(serverFacade);
        state = State.SIGNED_OUT;
    }

    public enum State {
        SIGNED_IN, SIGNED_OUT, PLAYING_GAME, OBSERVING, QUIT
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

            if (state.equals(State.SIGNED_OUT)) {
                System.out.println(pregameClient.eval(input));
                state = pregameClient.nextState();

            } else if (state.equals(State.SIGNED_IN)) {
                System.out.println(loggedinClient.eval(input));
                state = loggedinClient.nextState();

            } else if (state.equals(State.PLAYING_GAME)) {
                handleGamePlay(input);
            }
        }
        scanner.close();
    }


    private void handleGamePlay(String input) {
        String[] arguments = input.trim().split("\\s+");
        if (!validateArguments(arguments)) {
            System.out.println("Error: invalid command");
            help();
            return;
        }
        String cmd = arguments[0];
        switch (cmd.toLowerCase()) {
            case "move" -> moveHandler(arguments);
            case "help" -> help();
        }
    }

    private void moveHandler(String[] arguments) {
    }

    private void help() {
        if (state.equals(State.SIGNED_OUT)) {
            System.out.println(SET_TEXT_COLOR_RED +
                    """
                            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                            login <USERNAME> <PASSWORD> - to play chess
                            exit - to stop the program
                            help - with possible commands
                            
                            """
                    + RESET_TEXT_COLOR);
        } else if (state.equals(State.SIGNED_IN)) {
            System.out.print(SET_TEXT_COLOR_RED +
                    """
                            create <NAME> - a game
                            list - games
                            join <ID> <WHITE | BLACK> - a game
                            observe <ID> - a game;
                            logout - when you are done
                            exit - playing chess
                            help - with possible commands
                            
                            """
                    + RESET_TEXT_COLOR);
        } else if (state.equals(State.PLAYING_GAME)) {
            System.out.print(SET_TEXT_COLOR_RED +
                    """ 
                            redraw - the chess board
                            leave - the game
                            move <START_POSITION> <END_POSITION> [Promotion_Piece] - i.e. "move A2 A3" or "move A7 A8 queen"
                            resign - forfeit and end game
                            highlight <PIECE_POSITION> - highlight legal moves for a piece
                            exit - playing chess
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
        } else if (state == State.PLAYING_GAME) {
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
        }
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit")) {
            return arguments.length == 1;
        }
        return false;
    }
}

