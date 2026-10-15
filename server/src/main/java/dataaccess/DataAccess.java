package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    public void clear();
    public void register(UserData userData, AuthData authData);
    public UserData getUser(String username);
    public void createAuth(AuthData auth);
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);
}
