package client;

import ui.Repl;

public class ClientMain {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        new Repl(new ServerFacade(PORT)).run();
    }
}
