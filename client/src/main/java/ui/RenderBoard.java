package ui;

import chess.*;

import java.util.Collection;
import java.util.Map;

import static ui.EscapeSequences.*;

public class RenderBoard {
    private static final String WHT_COL_LABEL = "    a  b  c  d  e  f  g  h \n";
    private static final String BLK_COL_LABEL = "    h  g  f  e  d  c  b  a \n";

    public static String showBoardForWhite(ChessBoard board) {
        String boardString = WHT_COL_LABEL;
        for (int r = 8; r >= 1; r--) {
            boardString += (" " + r + " ");
            for (int c = 1; c <= 8; c++) {
                boardString += printSquare(r, c, board, null);
            }
            boardString += (" " + r + " \n");
        }
        return boardString += WHT_COL_LABEL;
    }

    public static String showBoardForBlack(ChessBoard board, Collection<ChessMove> validMoves) {
        String boardString = BLK_COL_LABEL;
        for (int r = 1; r <= 8; r++) {
            boardString += (" " + r + " ");
            for (int c = 8; c >= 1; c--) {
                boardString += printSquare(r, c, board, validMoves);
            }
            boardString += (" " + r + " \n");
        }
        return boardString += BLK_COL_LABEL;
    }

    private static String printSquare(int r, int c, ChessBoard board, Collection<ChessMove> validMoves) {
        var color = (r + c) % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                if (move.getEndPosition().equals(new ChessPosition(r, c))) {
                    color = SET_BG_COLOR_GREEN;
                    break;
                }
            }
        }
        ChessPiece piece = board.getPiece(new ChessPosition(r, c));
        return (color + getEscapeSequence(piece) + RESET_BG_COLOR);
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
            return SET_TEXT_COLOR_RED + blackSymbolsMap.get(piece.getPieceType()) + RESET_TEXT_COLOR;
        } else {
            return SET_TEXT_COLOR_BLUE + whiteSymbolsMap.get(piece.getPieceType()).toUpperCase() + RESET_TEXT_COLOR;
        }
    }
}

