package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.AdminService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final AdminService adminService;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        adminService = new AdminService(dataAccess);

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
