package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.getConnection;

public class DatabaseDataAccess implements DataAccess {

    public static void main(String[] args) throws DataAccessException {
        var DAO = new DatabaseDataAccess();
//        DAO.register(new UserData("user", "pw", "fake@yahoo.com"), new AuthData("auth-123456", "user"));
//        DAO.createAuth(new AuthData("auth-testing", "user"));

        var userData = DAO.getUser("user");
        var authData = DAO.getAuth("auth-testing");
//        DAO.createGame("testing");
        DAO.getGame(1);
        System.out.println(DAO.listGames());


    }

    public DatabaseDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = getConnection()) {
            //create an instance and configure the database, get the connection, set Catalog to the DB.
            //RETURN_GENERATED_KEYS gives you back the ID that the database creates. You need this for updates
            String[] CREATE_STATEMENTS = {"""
            CREATE TABLE IF NOT EXISTS users (
                 username VARCHAR(256) NOT NULL,
                 password VARCHAR(256) NOT NULL,
                 email VARCHAR(256) NOT NULL,
                 PRIMARY KEY (username)
             );""",

                    """
            CREATE TABLE IF NOT EXISTS auths (
                 auth VARCHAR(256) NOT NULL,
                 username VARCHAR(256) NOT NULL,
                 PRIMARY KEY (auth)
             );""",

                    """
            CREATE TABLE IF NOT EXISTS games(
                id INT NOT NULL AUTO_INCREMENT,
                wht_username VARCHAR(256),
                blk_username VARCHAR(256),
                game_name VARCHAR(256) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (id)
            )"""
            };
            for (var sql : CREATE_STATEMENTS) {
                try (var statement = conn.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to initialize");
        }


    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = getConnection()) {
            for (var sql : new String[]{
                    "TRUNCATE users;",
                    "TRUNCATE auths;",
                    "TRUNCATE games;"
            }) {
                try (var statement = conn.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
    }

    @Override
    public void register(UserData userData, AuthData authData) throws DataAccessException {
        try (var conn = getConnection();
             var usersStmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?);");
             var authsStmt = conn.prepareStatement("INSERT INTO auths (auth, username) VALUES (?, ?);")) {
            usersStmt.setString(1, userData.username());
            usersStmt.setString(2, userData.password());
            usersStmt.setString(3, userData.email());
            usersStmt.executeUpdate();

            authsStmt.setString(1, authData.authToken());
            authsStmt.setString(2, authData.username());
            authsStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to Register User");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?;")) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return (new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email")));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to getUser");
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("INSERT INTO auths (auth, username) VALUES (?, ?);")
        ) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to createAuth");
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM auths WHERE auth = ?;")) {
            stmt.setString(1, authToken);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return (new AuthData(rs.getString("auth"), rs.getString("username")));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("DELETE FROM auths WHERE auth = ?")) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games_list = new ArrayList<>();
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM games;")) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                GameData gameData = new GameData(rs.getInt("id"), rs.getString("wht_username"), rs.getString("blk_username"), rs.getString("game_name"), game);
                games_list.add(gameData);
            }
            return games_list;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games");
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("""
                     INSERT INTO games(game_name, game)
                     VALUES(?, ?);""")) {
            stmt.setString(1, gameName);
            stmt.setString(2, new Gson().toJson(new ChessGame()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game");
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM games WHERE id = ?")) {
            stmt.setInt(1, gameID);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                return new GameData(rs.getInt("id"), rs.getString("wht_username"), rs.getString("blk_username"), rs.getString("game_name"), game);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game");
        }
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement("UPDATE games SET game = ?, wht_username = ?, blk username = ? WHERE id = ?")) {
            stmt.setString(1, new Gson().toJson(updatedGame.game()));
            stmt.setString(2, updatedGame.whiteUsername());
            stmt.setString(3, updatedGame.blackUsername());
            stmt.setInt(4, updatedGame.gameID());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Update Game");
        }

    }
}
