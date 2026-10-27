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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;


public class DataAccessUnitTests {
    private static DataAccess dao;
    private static final UserData TEST_USER = new UserData("test-username", "test-pw", "test@gmail.com");
    private static final AuthData TEST_AUTH = new AuthData("test-auth", "test-username");


    @BeforeEach
    public void resetTests() throws DataAccessException {
        dao = new DatabaseDataAccess();
        dao.clear();
    }

    @Test
    public void clearWorks() throws DataAccessException {
        dao.register(TEST_USER, TEST_AUTH);
        dao.clear();

        Assertions.assertNull(dao.getUser(TEST_USER.username()));
        Assertions.assertNull(dao.getAuth(TEST_AUTH.authToken()));

    }

    @Test
    public void getUserTest() throws DataAccessException {
        dao.register(TEST_USER, TEST_AUTH);
        Assertions.assertEquals(TEST_USER.email(), dao.getUser(TEST_USER.username()).email());
        Assertions.assertNull(dao.getUser("badUsername"));
    }

    @Test
    public void duplicateRegistration() throws DataAccessException {
        dao.register(TEST_USER, TEST_AUTH);

        Assertions.assertThrows(DataAccessException.class, () ->
                dao.register(TEST_USER, TEST_AUTH));
    }

    @Test
    public void getAuthTest() throws DataAccessException {
        dao.createAuth(TEST_AUTH);

        Assertions.assertEquals(TEST_AUTH.username(), dao.getAuth(TEST_AUTH.authToken()).username());
        Assertions.assertNull(dao.getAuth("wrong-auth"));
    }

    @Test
    public void removeAuthTest() throws DataAccessException {
        dao.createAuth(TEST_AUTH);
        dao.deleteAuth(TEST_AUTH.authToken());

        Assertions.assertNull(dao.getAuth(TEST_AUTH.authToken()));
    }


    @Test
    public void createGameTest() throws DataAccessException {
        var gameID = dao.createGame("test-game");

        Assertions.assertEquals(1, gameID);
        Assertions.assertEquals(new ChessGame(), dao.getGame(gameID).game());
        Assertions.assertEquals(ChessGame.TeamColor.WHITE, dao.getGame(gameID).game().getTeamTurn());
        Assertions.assertNull(dao.getGame(gameID).whiteUsername());
        Assertions.assertNull(dao.getGame(gameID).blackUsername());

        var gameTwoID = dao.createGame("test-game");
        Assertions.assertEquals(2, gameTwoID);
    }

    @Test
    public void updateGameTest() throws DataAccessException, InvalidMoveException {
        var gameID = dao.createGame("test-game");
        var firstMove = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);
        var chessGame = new ChessGame();
        chessGame.makeMove(firstMove);
        dao.updateGame(new GameData(gameID, "white", "black", "test-game", chessGame));

        Assertions.assertEquals(1, dao.getGame(gameID).gameID());
        Assertions.assertEquals("white", dao.getGame(gameID).whiteUsername());
        Assertions.assertEquals("black", dao.getGame(gameID).blackUsername());
        Assertions.assertEquals("test-game", dao.getGame(gameID).gameName());
        Assertions.assertEquals(chessGame, dao.getGame(gameID).game());
    }


    @Test
    public void listGamesTest() throws DataAccessException, InvalidMoveException {
        int[] gameIDs = new int[5];
        for (int i = 0; i <= 4; i++) {
            var gameID = dao.createGame("test-game" + i);
            gameIDs[i] = gameID;
        }
        Collection<GameData> games = dao.listGames();

        Assertions.assertEquals(gameIDs.length, games.size());


    }

}
