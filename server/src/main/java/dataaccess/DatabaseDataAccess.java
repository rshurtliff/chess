package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.getConnection;

public class DatabaseDataAccess implements DataAccess {

    public static void main(String[] args) throws DataAccessException {
        var DAO = new DatabaseDataAccess();
        DAO.register(new UserData("user", "pw", "fake@yahoo.com"), new AuthData("auth-123456", "user"));

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
                //do stuff here
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
            throw new DataAccessException("Failed to Clear");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
        return List.of();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }
        return null;
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        try (var conn = getConnection()) {

        } catch (SQLException e) {
            throw new DataAccessException("Failed to Clear");
        }

    }
}
