package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        for (ChessPiece[] row : board) {
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
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // list the pieces out in order, then loop thru and fill the board up
        board = new ChessPiece[8][8];
        ChessPiece.PieceType[] pieceSchedule = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };
        final int whiteRow = 1;
        final int blackRow = 8;

        for (int col = 1; col <= 8; col++) {
            //adds the piece to board for both colors
            ChessPiece.PieceType piece = pieceSchedule[col - 1];
            addPiece(new ChessPosition(blackRow, col), new ChessPiece(ChessGame.TeamColor.BLACK, piece));
            addPiece(new ChessPosition(whiteRow, col), new ChessPiece(ChessGame.TeamColor.WHITE, piece));
            //add the pawns in both colors
            addPiece(new ChessPosition(blackRow - 1, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(whiteRow + 1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

    }

    /**
     * returns the position of the king, given a certain color
     */
    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        Collection<ChessPosition> filledPositions = findAllPieces(teamColor);
        for (ChessPosition pos : filledPositions){
            if (getPiece(pos).getPieceType() == ChessPiece.PieceType.KING){return pos;}
        }
        return null;
    }
    /**
     * returns the positions of all pieces of a certain color
     */
    public Collection<ChessPosition> findAllPieces(ChessGame.TeamColor teamColor) {
        Collection<ChessPosition> filledPositions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor){
                    filledPositions.add(pos);
                }
            }
        }
        return filledPositions;
    }

}

