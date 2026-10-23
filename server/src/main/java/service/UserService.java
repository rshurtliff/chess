package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, DataAccessException {
        // the service needs to get the UserData and do the business logic
        UserData user = dataAccess.getUser(registerRequest.username());
        if (user != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData auth = new AuthData(generateToken(), registerRequest.username());
        dataAccess.register(user, auth);
        return new RegisterResult(user.username(), auth.authToken());
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LoginResult login(LoginRequest loginRequest) throws NotAuthorizedException, DataAccessException {
        UserData user = dataAccess.getUser(loginRequest.username());
        if (isValidPassword(loginRequest, user)) {
            AuthData auth = new AuthData(generateToken(), loginRequest.username());
            dataAccess.createAuth(auth);
            return new LoginResult(loginRequest.username(), auth.authToken());
        } else {
            throw new NotAuthorizedException("Error: unauthorized");
        }

    }

    private boolean isValidPassword(LoginRequest loginRequest, UserData user) {
        return (user != null && loginRequest.password().equals(user.password()));
    }

    public void logout(LogoutRequest logoutRequest) throws NotAuthorizedException, DataAccessException {
        AuthData auth = dataAccess.getAuth(logoutRequest.authToken());
        if (auth == null) {
            throw new NotAuthorizedException("Error: unauthorized");
        }
        dataAccess.deleteAuth(logoutRequest.authToken());
    }
}
