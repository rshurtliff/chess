package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        Map<PieceType, String> symbolsMap = Map.of(
                PieceType.KING, "k",
                PieceType.QUEEN,"q",
                PieceType.ROOK, "r",
                PieceType.KNIGHT, "n",
                PieceType.BISHOP, "b",
                PieceType.PAWN,"p"
        );
        if (pieceColor == ChessGame.TeamColor.BLACK) return symbolsMap.get(type);
        else return symbolsMap.get(type).toUpperCase();
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        if (this.getPieceType() == PieceType.KING) {
            int[][] kingCoordinates = {{1,0},{0,1},{0,-1},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
            for (int[] move : kingCoordinates){
                int row = move[0]; int col = move[1];
                calculateMoves(board,myPosition,row,col,possibleMoves,false);
            }

        }

        if (this.getPieceType() == PieceType.QUEEN) {
            int[][] queenDirections = {{1,1},{-1,-1},{1,-1},{-1,1},{1,0},{-1,0},{0,-1},{0,1}};
            for (int[] move : queenDirections){
                int row = move[0]; int col = move[1];
                calculateMoves(board,myPosition, row,col,possibleMoves,true);
            }
        }

        if (this.getPieceType() == PieceType.BISHOP) {
            int[][] bishopDirections = {{1,1},{-1,-1},{1,-1},{-1,1}};
            for (int[] move : bishopDirections){
                int row = move[0]; int col = move[1];
                calculateMoves(board,myPosition, row,col,possibleMoves,true);
            }
        }

        if (this.getPieceType() == PieceType.KNIGHT) {
            int[][] knightCoordinates = {{2,1}, {1,2}, {-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
            for (int[] move : knightCoordinates){
                int row = move[0]; int col = move[1];
                calculateMoves(board,myPosition,row,col,possibleMoves,false);
            }
        }

        if (this.getPieceType() == PieceType.ROOK) {
            int[][] rookDirections = {{1,0},{-1,0},{0,-1},{0,1}};
            for (int[] move : rookDirections){
                int row = move[0]; int col = move[1];
                calculateMoves(board,myPosition, row,col,possibleMoves,true);
            }
        }

        if (this.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }

        return possibleMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        int rowPosition = myPosition.getRow();
        int colPosition = myPosition.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int homeRow;
        int movement;
        int promotionRow;
        PieceType[] promotionPieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        int[] attackCol = {-1,1};
         if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK){
             homeRow = 7;
             movement = -1;
             promotionRow = 1;
         } else {
             homeRow = 2;
             movement = 1;
             promotionRow = 8;
         }
        if (rowPosition == homeRow){ //home row cases (2 spaces only)
            if (board.getPiece(new ChessPosition(rowPosition + movement, colPosition)) == null &&
                 board.getPiece(new ChessPosition(rowPosition + movement * 2, colPosition)) == null){
                    possibleMoves.add(new ChessMove(myPosition,new ChessPosition(rowPosition + movement * 2, colPosition),null));
            }
        }
        // move forward
        if (board.getPiece(new ChessPosition(rowPosition + movement, colPosition)) == null){
            if(rowPosition != promotionRow - movement){ //if it won't lead to promotion
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowPosition + movement, colPosition), null));
            } else {
                for (PieceType piece : promotionPieces){
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(rowPosition + movement, colPosition), piece));
                }
            }
        }
        //capture piece if diagonal
        for (int direction : attackCol){ //check both ways
            if (colPosition + direction >= 1 && colPosition + direction <= 8){ //make sure not OOB
                ChessPosition attackPosition = new ChessPosition(rowPosition + movement, colPosition + direction);
                if (board.getPiece(attackPosition) != null && board.getPiece(attackPosition).getTeamColor() != this.getTeamColor()){ //see if it can capture
                    if (rowPosition != promotionRow - movement){ // if not the last row
                        possibleMoves.add(new ChessMove(myPosition, attackPosition,null));
                    } else {
                        for (PieceType piece : promotionPieces){
                            possibleMoves.add(new ChessMove(myPosition, attackPosition,piece));
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    public void calculateMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove,
                               ArrayList<ChessMove> possibleMoves, boolean allowDistance ){
        int row = myPosition.getRow() + rowMove;
        int col = myPosition.getColumn() + colMove;
        while (row <= 8 && row >= 1 && col <= 8 && col >= 1){ // while the move is inbounds:
            ChessPosition newPosition = new ChessPosition(row, col);
            if (board.getPiece(newPosition) == null){ //if the spot is empty
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                row += rowMove;
                col +=colMove;
            } else{
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){ //if it's occupied, check to see if you can capture it
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
            if (!allowDistance){break;}
        }
    }
}
