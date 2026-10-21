package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceUnitTests {
    private DataAccess dataAccess;
    private AdminService admin;
    private UserService user;
    RegisterRequest registerRequest;

    @BeforeEach
    public void prepareTests() {
        dataAccess = new MemoryDataAccess();
        admin = new AdminService(dataAccess);
        user = new UserService(dataAccess);
        registerRequest = new RegisterRequest("username", "password", "email@gmail.com");
    }

    @Test
    public void successfulRegistration() throws AlreadyTakenException {
        RegisterResult result = user.register(registerRequest);

        Assertions.assertEquals("username", result.username());
        Assertions.assertNotEquals(null, result.authToken());
        Assertions.assertEquals(registerRequest.username(), dataAccess.getUser("username").username());
    }

    @Test
    public void takenRegistration() throws AlreadyTakenException {
        user.register(registerRequest);
        Assertions.assertThrows(AlreadyTakenException.class, () -> user.register(registerRequest));
    }

    @Test
    public void successfulLogin() throws NotAuthorizedException, AlreadyTakenException {
        user.register(registerRequest);
        LoginResult result = user.login(new LoginRequest("username", "password"));
        Assertions.assertEquals("username", result.username());
        Assertions.assertNotEquals(null, result.authToken());
        Assertions.assertEquals("username", dataAccess.getAuth(result.authToken()).username());
    }

    @Test
    public void wrongPassword() throws AlreadyTakenException {
        user.register(registerRequest);
        Assertions.assertThrows(NotAuthorizedException.class, () ->
                user.login(new LoginRequest("username", "thisIsWrong")));
    }

    @Test
    public void successfulLogout() throws AlreadyTakenException, NotAuthorizedException {
        String authToken = user.register(registerRequest).authToken();
        user.logout(new LogoutRequest(authToken));

        Assertions.assertNull(dataAccess.getAuth(authToken));
    }

    @Test
    public void failedLogout() throws AlreadyTakenException {
        String authToken = "madeUpAuthToken";
        Assertions.assertThrows(NotAuthorizedException.class,
                () -> user.logout(new LogoutRequest(authToken)));

    }

    @Test
    public void joinGame() throws Exception {
        RegisterResult result = user.register(registerRequest);
        int gameID = admin.createGame(new CreateGameRequest("testGame", result.authToken()));

        admin.joinGame(new JoinGameRequest("WHITE", gameID), result.authToken());
        Assertions.assertEquals(dataAccess.getGame(gameID).whiteUsername(), result.username());
    }

    @Test
    public void joinBadGame() throws Exception {
        RegisterResult result = user.register(registerRequest);
        int gameID = admin.createGame(new CreateGameRequest("testGame", result.authToken()));
        Assertions.assertThrows(GameNotFoundException.class,
                () -> admin.joinGame(new JoinGameRequest("WHITE", 123456789), result.authToken()));
    }

    @Test
    public void clear() throws Exception {
        RegisterResult whiteResult = user.register(new RegisterRequest("whiteUser", "white", "white@gmail.com"));
        RegisterResult blackResult = user.register(new RegisterRequest("blackUser", "white", "white@gmail.com"));

        int gameID = admin.createGame(new CreateGameRequest("testGame", whiteResult.authToken()));

        admin.joinGame(new JoinGameRequest("WHITE", gameID), whiteResult.authToken());
        admin.joinGame(new JoinGameRequest("BLACK", gameID), blackResult.authToken());

        admin.clear();

        Assertions.assertTrue(dataAccess.listGames().isEmpty());
        Assertions.assertNull(dataAccess.getUser("whiteUser"));
        Assertions.assertNull(dataAccess.getUser("blackUser"));


    }

}
