package client;

import ui.Repl;

public class ClientMain {
    private static final int port = 8080;

    public static void main(String[] args) {
        new Repl(new ServerFacade(port)).run();
    }
}
