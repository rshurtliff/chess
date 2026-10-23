package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.getConnection;

public class DatabaseDataAccess implements DataAccess {
    //create an instance and configure the database, get the connection, set Catalog to the DB.
    //RETURN_GENERATED_KEYS gives you back the ID that the database creates. You need this for updates
    private String[] CREATE_STATEMENTS = {"""
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

    public DatabaseDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();

    }

    @Override
    public void clear() {

    }

    @Override
    public void register(UserData userData, AuthData authData) throws DataAccessException {
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {

    }
}
