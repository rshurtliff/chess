package ui;

public class LoggedInClient implements Client {
    @Override
    public String help() {
        return "";
    }

    @Override
    public Repl.State nextState() {
        return null;
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
