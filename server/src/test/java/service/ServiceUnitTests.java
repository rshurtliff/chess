package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.*;


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

}
