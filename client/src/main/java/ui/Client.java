package ui;

import ui.Repl.State;

public interface Client {
    String help();

    State nextState();

    String eval(String input);


}
