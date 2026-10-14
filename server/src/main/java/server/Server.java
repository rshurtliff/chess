package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {




        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", Server::clearDb);
        javalin.post("/user", Server::registerNewUser);
        javalin.post("/session",Server::login);
        javalin.delete("/session",Server::logout);
        javalin.get("/game",Server::listGames);
        javalin.post("/game",Server::createGame);
        javalin.put("/game",Server::joinGame);
        // Register your endpoints and exception handlers here.

    }




    private static void clearDb(Context ctx) {

        System.out.println("Testing Clear DB Endpoint");
    }

    private static void registerNewUser(Context ctx) {
        //register the new user
    }

    private static void login( Context ctx) {
        // login handler
    }

    private static void logout(Context ctx) {
    }

    private static void listGames(Context ctx) {
        System.out.println("Testing List Game Endpoint");

    }

    private static void createGame(Context ctx) {
    }

    private static void joinGame(Context ctx) {
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
