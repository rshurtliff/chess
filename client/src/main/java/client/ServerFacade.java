package client;

import com.google.gson.Gson;
import model.RegisterRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String url;
    private String authToken;

    public ServerFacade(int port) {
        url = "http://localhost:" + port;
    }


    public void register(String username, String password, String email) throws ResponseException {
        String json = new Gson().toJson(new RegisterRequest(username, password, email));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/user"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(json)) //send a RegisterRequest
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                authToken = new Gson().fromJson(response.body(), Map.class).get("authToken").toString();
            } else {
                throw new ResponseException(new Gson().fromJson(response.body(), Map.class).get("message").toString());
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Error: Couldn't connect to server");
        }
    }

    public void login(String username, String password) {

    }

}
