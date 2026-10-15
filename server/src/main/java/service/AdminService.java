package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class AdminService {
    private final DataAccess dataAccess;

    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }

    public Collection<GameData> listGames(String authToken) throws NotAuthorizedException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new NotAuthorizedException("Error: unauthorized");
        }
        return dataAccess.listGames();
    }

    public int createGame(CreateGameRequest createGameRequest) throws NotAuthorizedException {
        AuthData auth = dataAccess.getAuth(createGameRequest.authToken());
        if (auth == null) {
            throw new NotAuthorizedException("Error: unauthorized");
        }
        return dataAccess.createGame(createGameRequest.gameName());
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws NotAuthorizedException, GameNotFoundException, AlreadyTakenException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new NotAuthorizedException("Error: unauthorized");
        }
        GameData game = dataAccess.getGame(joinGameRequest.gameID());
        if (game == null) {
            throw new GameNotFoundException("Error: Game doesn't exist");
        }
        //see if the player is not taken
        if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
            if (game.whiteUsername() == null) {
                dataAccess.updateGame(new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game()));
            } else {
                throw new AlreadyTakenException("Error: WHITE already taken");
            }
        } else {
            if (game.blackUsername() == null) {
                dataAccess.updateGame(new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game()));
            } else {
                throw new AlreadyTakenException("Error: BLACK already taken");
            }
        }
    }
}
