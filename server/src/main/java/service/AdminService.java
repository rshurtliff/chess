package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class AdminService {
    private DataAccess dataAccess;

    public AdminService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void clear(){
        dataAccess.clear();
    }

    public Collection<GameData> listGames(String authToken) throws NotAuthorizedException{
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null){
            throw new NotAuthorizedException("Error: unauthorized");
        }
        return dataAccess.listGames();
    }

    public int createGame(CreateGameRequest createGameRequest) throws NotAuthorizedException{
        AuthData auth = dataAccess.getAuth(createGameRequest.authToken());
        if (auth == null){
            throw new NotAuthorizedException("Error: unauthorized");
        }
        return dataAccess.createGame(createGameRequest.gameName());
    }
}
