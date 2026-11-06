package client;

import com.google.gson.Gson;
import model.RegisterRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String url;

    public ServerFacade(int port) {
        url = "http://localhost:" + port;
    }


    public void register(String username, String password, String email) {
        String json = new Gson().toJson(new RegisterRequest(username, password, email));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/user"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(json)) //send a RegisterRequest
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Registration Successful!" + response.body());
            }
        } catch (URISyntaxException e) {
            System.out.println("Error: Couldn't connect to server");
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: bad request");
        }
    }

    public void login(String username, String password) {

    }

}
