package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.sql.DatabaseDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.GameData;
import service.*;

import java.util.Collection;
import java.util.Map;

public class Server {
    private static final Gson SERIALIZER = new Gson();
    private final Javalin javalin;
    private final AdminService adminService;
    private final UserService userService;


    public Server() {
        try {
            DataAccess dataAccess = new DatabaseDataAccess();
            adminService = new AdminService(dataAccess);
            userService = new UserService(dataAccess);
        } catch (DataAccessException e) {
            throw new RuntimeException("couldn't get conection to Database");
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", this::clearDb);
        javalin.post("/user", this::registerNewUser);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout); // START HERE, above is done
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }


    private void clearDb(Context ctx) throws DataAccessException {
        try {
            adminService.clear();
            ctx.status(200);
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
        }
    }

    private void registerNewUser(Context ctx) {
        RegisterRequest registerRequest = SERIALIZER.fromJson(ctx.body(), RegisterRequest.class);
        if (isBadRegisterRequest(registerRequest)) {
            ctx.status(400);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.status(200);
            ctx.result(SERIALIZER.toJson(registerResult));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: username already taken")));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
        }
    }

    private boolean isBadRegisterRequest(RegisterRequest registerRequest) {
        return (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null);
    }

    private void login(Context ctx) {
        LoginRequest loginRequest = SERIALIZER.fromJson(ctx.body(), LoginRequest.class);
        if (isBadLoginRequest(loginRequest)) {
            ctx.status(400);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        try {
            LoginResult loginResult = userService.login(loginRequest);
            ctx.status(200);
            ctx.result(SERIALIZER.toJson(loginResult));

        } catch (NotAuthorizedException e) {
            ctx.status(401);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: unauthorized")));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
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
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: unauthorized")));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
        }
    }


    private void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        try {
            Collection<GameData> games = adminService.listGames(authToken);
            ctx.status(200);
            ctx.result(SERIALIZER.toJson(Map.of("games", games)));
        } catch (NotAuthorizedException e) {
            ctx.status(401);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: unauthorized")));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
        }
    }


    private void createGame(Context ctx) {
        String authToken = ctx.header("authorization");
        GameData game = SERIALIZER.fromJson(ctx.body(), GameData.class);
        if (game.gameName() == null) {
            ctx.status(400);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        try {
            int gameID = adminService.createGame(new CreateGameRequest(game.gameName(), authToken));
            ctx.status(200);
            ctx.result(SERIALIZER.toJson(Map.of("gameID", gameID)));
        } catch (NotAuthorizedException e) {
            ctx.status(401);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: unauthorized")));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
        }
    }

    private void joinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JoinGameRequest joinGameRequest = SERIALIZER.fromJson(ctx.body(), JoinGameRequest.class);
        if (isBadJoinGameRequest(joinGameRequest)) {
            ctx.status(400);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        try {
            adminService.joinGame(joinGameRequest, authToken);
        } catch (NotAuthorizedException e) {
            ctx.status(401);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: unauthorized")));
        } catch (GameNotFoundException e) {
            ctx.status(400);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: bad request")));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: already taken")));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(SERIALIZER.toJson(Map.of("message", "Error: Internal Server Error")));
        }
    }

    private boolean isBadJoinGameRequest(JoinGameRequest joinGameRequest) {
        return (joinGameRequest.gameID() == 0 || joinGameRequest.playerColor() == null ||
                (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK")));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
