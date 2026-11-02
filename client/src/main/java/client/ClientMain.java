package client;

import chess.*;

import static ui.EscapeSequences.*;

public class ClientMain {
    private static final String COL_LABEL = "    a  b  c  d  e  f  g  h \n";

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        printBoard(new ChessGame().getBoard());
    }

    private static void printBoard(ChessBoard board) {
        System.out.print(COL_LABEL);
        for (int r = 1; r <= 8; r++) {
            System.out.print(" " + (9 - r) + " ");
            for (int c = 1; c <= 8; c++) {
                var color = (r + c) % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                System.out.print(color + " " + getEscapeSequence(piece) + " " + RESET_BG_COLOR);
            }
            System.out.print(" " + (9 - r) + " ");
            System.out.print("\n");
        }
        System.out.print(COL_LABEL);
    }

    private static String getEscapeSequence(ChessPiece piece) {
        return " ";
    }

}
