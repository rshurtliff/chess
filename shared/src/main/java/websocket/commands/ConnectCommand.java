package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final Role role;

    public ConnectCommand(String authToken, Integer gameID, Role role) {
        super(CommandType.CONNECT, authToken, gameID);
        this.role = role;
    }

    public enum Role {
        WHITE, BLACK, OBSERVER
    }

    public Role getRole() {
        return role;
    }
}
