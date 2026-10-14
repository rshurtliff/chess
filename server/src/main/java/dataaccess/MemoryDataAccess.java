package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final  Map<String, AuthData> auths = new HashMap<>();
    private final Map<String, GameData> games = new HashMap<>();

    @Override
    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void register(UserData userData, AuthData authData){
        users.put(userData.username(), userData);
        auths.put(authData.authToken(), authData);
    }

    /**Finds the user
     by username @returns a UserData object*/
    @Override
    public UserData getUser(String username){
        return users.get(username);
    }


    }


