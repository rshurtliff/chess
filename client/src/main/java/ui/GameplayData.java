package ui;

import chess.ChessGame;
import model.GameData;

public record GameplayData(GameData gameData, ChessGame.TeamColor pointOfView) {
}
