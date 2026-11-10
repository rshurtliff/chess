package client;

import org.glassfish.grizzly.http.server.Response;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);

    }

    @BeforeEach
    void setup() {
        try {
            facade.clear();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerGood() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        Assertions.assertNotNull(facade.getAuthToken());
    }

    @Test
    public void registerBad() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        Assertions.assertThrows(ResponseException.class, () ->
                facade.register("TestUser", "notsecure", "fake@gmail.com"));

    }

    @Test
    public void logoutGood() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        facade.logout();
        Assertions.assertNull(facade.getAuthToken());
    }

    @Test
    public void loginGood() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        facade.logout();
        facade.login("TestUser", "notsecure");
        Assertions.assertNotNull(facade.getAuthToken());
    }

    @Test
    public void loginBad() {
        Assertions.assertThrows(ResponseException.class,
                () -> facade.login("TestUser", "notsecure"));
    }

    @Test
    public void createGameGood() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        Assertions.assertEquals(1, facade.createGame("chicken"));
    }


    @Test
    public void listGames() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        facade.createGame("game1");
        facade.createGame("game2");
        facade.createGame("game3");
        var games = facade.listGames();
        Assertions.assertEquals("game1", games.get(0).gameName());
        Assertions.assertEquals("game2", games.get(1).gameName());
        Assertions.assertEquals("game3", games.get(2).gameName());
    }

    @Test
    public void joinGameGood() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        facade.createGame("testgame");
        facade.joinGame(1, "BLACK");
        Assertions.assertEquals("TestUser", facade.listGames().get(0).blackUsername());
    }

    @Test
    public void joinGameBad() throws ResponseException {
        facade.register("TestUser", "notsecure", "fake@gmail.com");
        facade.createGame("testgame");
        Assertions.assertThrows(ResponseException.class, () ->
                facade.joinGame(0, "BLACK"));
    }

}
