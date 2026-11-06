package client;

import ui.Repl;

public class ClientMain {
    private static final String URL = "http://localhost:8080";

    public static void main(String[] args) {
        new Repl(new ServerFacade(URL)).run();
    }
}
