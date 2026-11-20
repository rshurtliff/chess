package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class LoggedInClient implements Client {
    private final ServerFacade serverFacade;
    private Repl.State nextState;
    private GameplayData currentGame;

    public LoggedInClient(ServerFacade serverFacade, GameplayData currentGame) {
        this.serverFacade = serverFacade;
        nextState = Repl.State.SIGNED_IN;
        this.currentGame = currentGame;
    }

    @Override
    public String help() {
        return (SET_TEXT_COLOR_RED +
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
    }

    @Override
    public Repl.State nextState() {
        return nextState;
    }

    @Override
    public String eval(String input) {
        nextState = Repl.State.SIGNED_IN;
        String[] arguments = input.trim().split("\\s+");
        if (!validateArguments(arguments)) {
            help();
            return ("Error: invalid command\n" + help());
        }
        String cmd = arguments[0];
        return switch (cmd.toLowerCase()) {
            case "help" -> help();
            case "create" -> createGameHandler(arguments);
            case "list" -> listHandler();
            case "join" -> joinHandler(arguments);
            case "observe" -> observerHandler(arguments);
            case "logout" -> logoutHandler();
            case "exit" -> exitHandler();
            default -> "Unknown Command!";
        };
    }

    private boolean validateArguments(String[] arguments) {
        if (arguments.length == 0) {
            return false;
        }
        String cmd = arguments[0];
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
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit")) {
            return arguments.length == 1;
        }
        return false;
    }

    private String createGameHandler(String[] arguments) {
        try {
            int gameID = serverFacade.createGame(arguments[1]);
            return ("Game successfully created! ID: " + gameID);
        } catch (ResponseException e) {
            return (e.getMessage());
        }
    }

    private String listHandler() {
        try {
            ArrayList<GameData> games = serverFacade.listGames();
            String gamesString = "";
            for (int i = 1; i <= games.size(); i++) {
                GameData game = games.get(i - 1);
                var whtUser = (game.whiteUsername() == null) ? "None" : game.whiteUsername();
                var blkUser = (game.blackUsername() == null) ? "None" : game.blackUsername();
                gamesString += (i + ": " + game.gameName() + " | WHITE PLAYER: " + whtUser +
                        " | BLACK PLAYER: " + blkUser + "\n");
            }
            return gamesString;
        } catch (ResponseException e) {
            return (e.getMessage());
        }
    }

    private String joinHandler(String[] arguments) {
        int gameID;
        String playerColor;
        try {
            gameID = Integer.parseInt(arguments[1]);
            playerColor = arguments[2].toUpperCase();

        } catch (NumberFormatException e) {
            return ("Error: GameID must be an integer");
        }
        try {
            serverFacade.joinGame(gameID, playerColor);
            var games = serverFacade.listGames();
            nextState = Repl.State.PLAYING_GAME;
            currentGame = new GameplayData(games.get(gameID - 1), (playerColor.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK));
            return ("Successfully joined game " + gameID + " as " + playerColor);
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    private String observerHandler(String[] arguments) {
        int gameID;
        try {
            gameID = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException e) {
            return ("Error: GameID must be an integer");
        }
        try {
            var games = serverFacade.listGames();
            nextState = Repl.State.OBSERVING;
            currentGame = new GameplayData(lookupGame(gameID), ChessGame.TeamColor.WHITE);
            return RenderBoard.showBoardForWhite(games.get(gameID - 1).game().getBoard(), null);
        } catch (ResponseException e) {
            return (e.getMessage());
        }
    }

    private String logoutHandler() {
        try {
            serverFacade.logout();
            nextState = Repl.State.SIGNED_OUT;
            return ("Successfully logged out!");
        } catch (ResponseException e) {
            return (e.getMessage());
        }
    }

    private String exitHandler() {
        nextState = Repl.State.QUIT;
        return ("Goodbye");
    }

    private GameData lookupGame(int gameID) {
        try {
            var games = serverFacade.listGames();
            return games.get(gameID - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public GameplayData getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameplayData gameData) {
        currentGame = gameData;
    }

}
