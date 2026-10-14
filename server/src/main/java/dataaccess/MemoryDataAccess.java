package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private Map<String, UserData> users = new HashMap<>();
    private Map<String, AuthData> auths = new HashMap<>();
    private Map<String, GameData> games = new HashMap<>();

    @Override
    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void register(UserData userData, AuthData authData){
        users.put(userData.getUsername(), userData);
        auths.put(authData.getAuth(), authData);
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

}
