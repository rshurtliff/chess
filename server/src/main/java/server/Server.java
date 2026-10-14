package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final AdminService adminService;
    private final UserService userService;


    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        adminService = new AdminService(dataAccess);
        userService = new UserService(dataAccess);

        // Register your endpoints and exception handlers here.
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::clearDb);
        javalin.post("/user", this::registerNewUser);
        javalin.post("/session",this::login);
        javalin.delete("/session",this::logout);
        javalin.get("/game",this::listGames);
        javalin.post("/game",this::createGame);
        javalin.put("/game",this::joinGame);
    }




    private void clearDb(Context ctx) {
        adminService.clear();
        System.out.println("Testing Clear DB Endpoint");
        ctx.status(200);

    }

    private void registerNewUser(Context ctx) {
        //register the new user
        // body is a JSON string. Create a RegisterRequest object (make the class first, maybe a record actually), and then proceed down the sequence diagram
        var serializer = new Gson();
        var registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);

        System.out.println(registerRequest);

        try {
           RegisterResult registerResult = userService.register(registerRequest);
           ctx.status(200);
           ctx.result(serializer.toJson(registerResult));

            System.out.println(registerResult);

        } catch (AlreadyTakenException e){
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message","Error: username already taken")));
            System.out.println(e.getMessage());
        }


    }

    private void login( Context ctx) {
        // login handler
    }

    private void logout(Context ctx) {
    }

    private void listGames(Context ctx) {
        System.out.println("Testing List Game Endpoint");

    }

    private void createGame(Context ctx) {
    }

    private void joinGame(Context ctx) {
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
