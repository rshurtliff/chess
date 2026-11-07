package client;

import com.google.gson.Gson;
import model.LoginRequest;
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

    public void logout() throws ResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/session"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .header("authorization", authToken)
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                authToken = null;
            } else {
                throw new ResponseException(new Gson().fromJson(response.body(), Map.class).get("message").toString());
            }
        } catch (InterruptedException | URISyntaxException | IOException e) {
            System.out.println("Error: Couldn't connect to to server");
        }
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

    public void login(String username, String password) throws ResponseException {
        String json = new Gson().toJson(new LoginRequest(username, password));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/session"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
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
}


