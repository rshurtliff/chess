package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.sql.DatabaseDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.sql.DatabaseManager.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;


public class DataAccessUnitTests {
    private DataAccess DAO;
    private static final UserData TEST_USER = new UserData("test-username", "test-pw", "test@gmail.com");
    private static final AuthData TEST_AUTH = new AuthData("test-auth", "test-username");


    @BeforeEach
    public void resetTests() throws DataAccessException {
        DAO = new DatabaseDataAccess();
        DAO.clear();
    }

    @Test
    public void clearWorks() throws DataAccessException {
        DAO.register(TEST_USER, TEST_AUTH);
        DAO.clear();

        Assertions.assertNull(DAO.getUser(TEST_USER.username()));
        Assertions.assertNull(DAO.getAuth(TEST_AUTH.authToken()));

    }

    @Test
    public void getUserTest() throws DataAccessException {
        DAO.register(TEST_USER, TEST_AUTH);
        Assertions.assertEquals(TEST_USER.email(), DAO.getUser(TEST_USER.username()).email());
        Assertions.assertNull(DAO.getUser("badUsername"));
    }

    @Test
    public void duplicateRegistration() throws DataAccessException {
        DAO.register(TEST_USER, TEST_AUTH);

        Assertions.assertThrows(DataAccessException.class, () ->
                DAO.register(TEST_USER, TEST_AUTH));
    }

    @Test
    public void getAuthTest() throws DataAccessException {
        DAO.createAuth(TEST_AUTH);

        Assertions.assertEquals(TEST_AUTH.username(), DAO.getAuth(TEST_AUTH.authToken()).username());
        Assertions.assertNull(DAO.getAuth("wrong-auth"));
    }

    @Test
    public void removeAuthTest() throws DataAccessException {
        DAO.createAuth(TEST_AUTH);
        DAO.deleteAuth(TEST_AUTH.authToken());

        Assertions.assertNull(DAO.getAuth(TEST_AUTH.authToken()));
    }


    @Test
    public void createGameTest() throws DataAccessException {
        var gameID = DAO.createGame("test-game");

        Assertions.assertEquals(1, gameID);
        Assertions.assertEquals(new ChessGame(), DAO.getGame(gameID).game());
        Assertions.assertEquals(ChessGame.TeamColor.WHITE, DAO.getGame(gameID).game().getTeamTurn());
        Assertions.assertNull(DAO.getGame(gameID).whiteUsername());
        Assertions.assertNull(DAO.getGame(gameID).blackUsername());

        var gameTwoID = DAO.createGame("test-game");
        Assertions.assertEquals(2, gameTwoID);
    }

    @Test
    public void updateGameTest() throws DataAccessException, InvalidMoveException {
        var gameID = DAO.createGame("test-game");
        var firstMove = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);
        var chessGame = new ChessGame();
        chessGame.makeMove(firstMove);
        DAO.updateGame(new GameData(gameID, "white", "black", "test-game", chessGame));

        Assertions.assertEquals(1, DAO.getGame(gameID).gameID());
        Assertions.assertEquals("white", DAO.getGame(gameID).whiteUsername());
        Assertions.assertEquals("black", DAO.getGame(gameID).blackUsername());
        Assertions.assertEquals("test-game", DAO.getGame(gameID).gameName());
        Assertions.assertEquals(chessGame, DAO.getGame(gameID).game());
    }


    @Test
    public void listGamesTest() throws DataAccessException, InvalidMoveException {
        int[] gameIDs = new int[5];
        for (int i = 0; i <= 4; i++) {
            var gameID = DAO.createGame("test-game" + i);
            gameIDs[i] = gameID;
        }
        Collection<GameData> games = DAO.listGames();

        Assertions.assertEquals(gameIDs.length, games.size());


    }

}
