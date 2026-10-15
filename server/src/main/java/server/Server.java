package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class Server {
    private static final Gson serializer = new Gson();
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
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout); // START HERE, above is done
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }


    private void clearDb(Context ctx) {
        adminService.clear();
        ctx.status(200);

    }

    private void registerNewUser(Context ctx) {
        RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
        if (isBadRegisterRequest(registerRequest)) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(registerResult));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message", "Error: username already taken")));
        }
    }

    private boolean isBadRegisterRequest(RegisterRequest registerRequest) {
        return (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null);
    }

    private void login(Context ctx) {
        LoginRequest loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
        if (isBadLoginRequest(loginRequest)) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        try {
            LoginResult loginResult = userService.login(loginRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(loginResult));

        } catch (NotAuthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
        }
    }

    private boolean isBadLoginRequest(LoginRequest loginRequest) {
        return (loginRequest.username() == null || loginRequest.password() == null);
    }

    private void logout(Context ctx) {
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
        try {
            userService.logout(logoutRequest);
            ctx.status(200);
        } catch (NotAuthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
        }
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
