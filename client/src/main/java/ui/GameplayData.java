package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;

import java.util.Objects;

public final class GameplayData {
    private GameData gameData;
    private final ChessGame.TeamColor pointOfView;

    public GameplayData(GameData gameData, ChessGame.TeamColor pointOfView) {
        this.gameData = gameData;
        this.pointOfView = pointOfView;
    }

    public GameData gameData() {
        return gameData;
    }

    public ChessGame.TeamColor pointOfView() {
        return pointOfView;
    }

    public void updateGame(ChessGame newGame) {
        GameData old = gameData;
        gameData = new GameData(old.gameID(), old.whiteUsername(), old.blackUsername(), old.gameName(), newGame);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (GameplayData) obj;
        return Objects.equals(this.gameData, that.gameData) &&
                Objects.equals(this.pointOfView, that.pointOfView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameData, pointOfView);
    }

    @Override
    public String toString() {
        return "GameplayData[" +
                "gameData=" + gameData + ", " +
                "pointOfView=" + pointOfView + ']';
    }

}
