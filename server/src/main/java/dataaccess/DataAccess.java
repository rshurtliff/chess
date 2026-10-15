package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;

public interface DataAccess {
    void clear();

    void register(UserData userData, AuthData authData);

    UserData getUser(String username);

    void createAuth(AuthData auth);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    Collection<GameData> listGames();

    int createGame(String gameName);
}
