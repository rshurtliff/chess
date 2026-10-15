package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final  Map<String, AuthData> auths = new HashMap<>();
    private final Map<String, GameData> games = new HashMap<>();
    private int gameIDCounter = 1;

    @Override
    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void register(UserData user, AuthData auth){
        users.put(user.username(), user);
        auths.put(auth.authToken(), auth);
    }

    /**Finds the user
     by username @returns a UserData object*/
    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth){
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public int createGame(String gameName) {
        games.put(gameName, new GameData(gameIDCounter,null,null,gameName,new ChessGame()));
        return gameIDCounter++;
    }


}




