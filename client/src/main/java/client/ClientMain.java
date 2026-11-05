package client;

import chess.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.RenderBoard.*;

public class ClientMain {

    public static void main(String[] args) {
        System.out.println("Welcome to chess for CS 240. Type " + SET_TEXT_COLOR_RED + "help" + RESET_TEXT_COLOR + " to get started");
        while (true) {
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            String[] arguments = input.trim().split("\\s+");
            if (!validateArguments(arguments)) {
                System.out.println("Error: invalid command");
                help();
            }

            String cmd = arguments[0];

            switch (input.toLowerCase()) {
                case "help" -> help();
                case "register" -> register(arguments);
                case "login" -> login(arguments);
            }
        }

    }

    private static void help() {
        System.out.println(SET_TEXT_COLOR_RED + "register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("exit - to stop the program");
        System.out.println("help - to list possible commands\n" + RESET_TEXT_COLOR);
    }

    private static void register(String[] args) {
        System.out.println(Arrays.toString(args));
    }

    private static void login(String[] args) {
        System.out.println(Arrays.toString(args));

    }

    private static boolean validateArguments(String[] arguments) {
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
        return cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("exit");

    }
}
