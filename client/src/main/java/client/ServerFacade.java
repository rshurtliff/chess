package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.GameData;
import model.JoinGameRequest;
import model.LoginRequest;
import model.RegisterRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.util.ArrayList;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final String url;
    private String authToken;

    public ServerFacade(int port) {
        url = "http://localhost:" + port;
    }

    public void clear() throws ResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/db"))
                    .DELETE()
                    .build();
            HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | URISyntaxException | IOException e) {
            throw new ResponseException("Error: Couldn't connect to to server");
        }
    }

    public void joinGame(int gameID, String color) throws ResponseException {
        String json;
        json = new Gson().toJson(new JoinGameRequest(color, gameID));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/game"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("authorization", authToken)
                    .build();
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            // do something with the reponse now that you hit the server
            if (response.statusCode() != 200) {
                throw new ResponseException(new Gson().fromJson(response.body(), Map.class).get("message").toString());
            }
        } catch (InterruptedException | URISyntaxException | IOException e) {
            throw new ResponseException("Error: Couldn't connect to to server");
        }

    }

    public ArrayList<GameData> listGames() throws ResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/game"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .GET()
                    .header("authorization", authToken)
                    .build();
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ResponseException(new Gson().fromJson(response.body(), Map.class).get("message").toString());
            }
            return new Gson().fromJson(response.body(), GamesList.class).games();
        } catch (InterruptedException | URISyntaxException | IOException e) {
            throw new ResponseException("Error: Couldn't connect to to server");
        }
    }

    public int createGame(String gameName) throws ResponseException {
        String json = new Gson().toJson(Map.of("gameName", gameName));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/game"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("authorization", authToken)
                    .build();
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ResponseException(new Gson().fromJson(response.body(), Map.class).get("message").toString());
            }
            return new Gson().fromJson(response.body(), JsonObject.class).get("gameID").getAsInt();
        } catch (InterruptedException | URISyntaxException | IOException e) {
            throw new ResponseException("Error: Couldn't connect to to server");
        }
    }

    public void logout() throws ResponseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/session"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .header("authorization", authToken)
                    .build();
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
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
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
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
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                authToken = new Gson().fromJson(response.body(), Map.class).get("authToken").toString();
            } else {
                throw new ResponseException(new Gson().fromJson(response.body(), Map.class).get("message").toString());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Error: Couldn't connect to server");
        }
    }

    public String getAuthToken() {
        return authToken;
    }
}


