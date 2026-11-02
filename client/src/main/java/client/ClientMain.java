package client;

import ui.EscapeSequences.*;
import chess.*;

import static ui.EscapeSequences.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        printBoard();
    }

    private static void printBoard() {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                var color = (r + c) % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                System.out.print(color + " " + "T" + " " + RESET_BG_COLOR);
            }
            System.out.print("\n");
        }
    }
}
