package client;

import chess.*;
import ui.Repl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.RenderBoard.*;

public class ClientMain {

    public static void main(String[] args) {
        new Repl(new ServerFacade()).run();
    }
}
