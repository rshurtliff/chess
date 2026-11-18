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
    private final LoggedInClient loggedinClient;
    private final GamePlayClient gamePlayClient;

    public Repl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        pregameClient = new PregameClient(serverFacade);
        loggedinClient = new LoggedInClient(serverFacade, null);
        gamePlayClient = new GamePlayClient(serverFacade, null);

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
                gamePlayClient.setCurrentGame(loggedinClient.getCurrentGame());

            } else if (state.equals(State.PLAYING_GAME)) {
                System.out.println(gamePlayClient.eval(input));
                state = gamePlayClient.nextState();
                loggedinClient.setCurrentGame((gamePlayClient.getCurrentGame()));
            }
        }
        scanner.close();
    }

}

