package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8]; //9 by 9 if you want to be clever? Then you don't have to subtract 1


    public ChessBoard() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ChessPiece[] row : board){
            sb.append(Arrays.toString(row)).append("\n");
        }
        return "ChessBoard: \n" +
                sb.toString();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() -1 ][position.getColumn() -1 ] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() -1 ][position.getColumn() -1 ];

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
       // list the pieces out in order, then loop thru and fill the board up
        ChessPiece.PieceType[] pieceSchedule = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };
        final int whiteRow = 1;
        final int blkRow = 8;
        int col = 1;

        for (ChessPiece.PieceType piece : pieceSchedule){
            //adds the piece to board for both colors
            this.addPiece(new ChessPosition(blkRow, col), new ChessPiece(ChessGame.TeamColor.BLACK, piece));
            this.addPiece(new ChessPosition(whiteRow, col), new ChessPiece(ChessGame.TeamColor.WHITE, piece));
            //add the pawns in both colors
            this.addPiece(new ChessPosition(blkRow -1 , col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(whiteRow + 1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            col++;

        }

    }
}
