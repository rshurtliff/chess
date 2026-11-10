package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Map;

import static ui.EscapeSequences.*;

public class RenderBoard {
    private static final String WHT_COL_LABEL = "    a  b  c  d  e  f  g  h \n";
    private static final String BLK_COL_LABEL = "    h  g  f  e  d  c  b  a \n";

    public static void printBoardForWhite(ChessBoard board) {
        System.out.print(WHT_COL_LABEL);
        for (int r = 8; r >= 1; r--) {
            System.out.print(" " + r + " ");
            for (int c = 1; c <= 8; c++) {
                printSquare(r,c,board);
            }
            System.out.print(" " + r + " ");
            System.out.print("\n");
        }
        System.out.print(WHT_COL_LABEL);
    }

    public static void printBoardForBlack(ChessBoard board) {
        System.out.print(BLK_COL_LABEL);
        for (int r = 1; r <= 8; r++) {
            System.out.print(" " + r + " ");
            for (int c = 8; c >= 1; c--) {
                printSquare(r,c,board);
            }
            System.out.print(" " + r + " ");
            System.out.print("\n");
        }
        System.out.print(BLK_COL_LABEL);
    }

    private static void printSquare(int r, int c, ChessBoard board){
        var color = (r + c) % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
        ChessPiece piece = board.getPiece(new ChessPosition(r, c));
        System.out.print(color + getEscapeSequence(piece) + RESET_BG_COLOR);
    }

    private static String getEscapeSequence(ChessPiece piece) {
        Map<ChessPiece.PieceType, String> blackSymbolsMap = Map.of(
                ChessPiece.PieceType.KING, BLACK_KING,
                ChessPiece.PieceType.QUEEN, BLACK_QUEEN,
                ChessPiece.PieceType.ROOK, BLACK_ROOK,
                ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
                ChessPiece.PieceType.BISHOP, BLACK_BISHOP,
                ChessPiece.PieceType.PAWN, BLACK_PAWN
        );
        Map<ChessPiece.PieceType, String> whiteSymbolsMap = Map.of(
                ChessPiece.PieceType.KING, WHITE_KING,
                ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
                ChessPiece.PieceType.ROOK, WHITE_ROOK,
                ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
                ChessPiece.PieceType.BISHOP, WHITE_BISHOP,
                ChessPiece.PieceType.PAWN, WHITE_PAWN
        );
        if (piece == null) {
            return EMPTY;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return blackSymbolsMap.get(piece.getPieceType());
        } else {
            return whiteSymbolsMap.get(piece.getPieceType()).toUpperCase();
        }
    }
}

