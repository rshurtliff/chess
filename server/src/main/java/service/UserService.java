package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException{
         // the service needs to get the UserData and do the business logic
         UserData user = dataAccess.getUser(registerRequest.username());
         if(user != null){
             throw new AlreadyTakenException("Error: username already taken");
         }
         user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
         AuthData auth = new AuthData(generateToken(), registerRequest.username());
         dataAccess.register(user, auth);
         return new RegisterResult(user.username(), auth.authToken());
    }

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
