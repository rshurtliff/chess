package client;

import java.net.http.*;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String url;

    public ServerFacade(int port) {
        url = "http://localhost:" + port;
    }
}
