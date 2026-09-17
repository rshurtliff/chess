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
            return setMoves(board, myPosition, kingCoordinates);
        }

        if (this.getPieceType() == PieceType.QUEEN) {
            Collection<ChessMove> queenMoves = this.rookMoves(board, myPosition);
            queenMoves.addAll(this.bishopMoves(board, myPosition));
            return queenMoves;
        }

        if (this.getPieceType() == PieceType.BISHOP) {
            return this.bishopMoves(board, myPosition);
        }

        if (this.getPieceType() == PieceType.KNIGHT) {
            int[][] knightCoordinates = {{2,1}, {1,2}, {-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
            return setMoves(board,myPosition, knightCoordinates);
        }

        if (this.getPieceType() == PieceType.ROOK) {
            return this.rookMoves(board, myPosition);
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

    public Collection<ChessMove> setMoves(ChessBoard board, ChessPosition myPosition,int[][] moveCoordinates){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int rowPosition = myPosition.getRow();
        int colPosition = myPosition.getColumn();
        for (int[] move : moveCoordinates){
            int newRow = move[0] + rowPosition;
            int newCol = move[1] + colPosition;
            if (newRow > 8 || newRow < 1 || newCol > 8 || newCol < 1) continue; //OOB Check
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){
                possibleMoves.add(new ChessMove(myPosition,newPosition,null));
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rowPosition = myPosition.getRow();
        int colPosition = myPosition.getColumn();
        //check the piece forward
        int rowsForward = 1;
        while (true) {
            if (rowPosition + rowsForward > 8) break; //check if OOB
            ChessPosition newFwdPosition = new ChessPosition(rowPosition + rowsForward, colPosition);
            if (board.getPiece(newFwdPosition) == null) { //check if spot is empty
                possibleMoves.add(new ChessMove(myPosition, newFwdPosition, null));
                rowsForward++;
            } else {
                if (board.getPiece(newFwdPosition).getTeamColor() != this.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, newFwdPosition, null));
                }
                break;
            }
        }
        //check backwards
        int rowsBack = 1;
        while (true) {
            if (rowPosition - rowsBack < 1) break; //check if OOB
            ChessPosition newBackPosition = new ChessPosition(rowPosition - rowsBack, colPosition);
            if (board.getPiece(newBackPosition) == null) { //check if spot is empty
                possibleMoves.add(new ChessMove(myPosition, newBackPosition, null));
                rowsBack++;
            } else {
                if (board.getPiece(newBackPosition).getTeamColor() != this.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, newBackPosition, null));
                }
                break;
            }
        }
        //check Right
        int colsRight = 1;
        while (true) {
            if (colPosition + colsRight > 8) break; //check if OOB
            ChessPosition newPosition = new ChessPosition(rowPosition, colPosition + colsRight);
            if (board.getPiece(newPosition) == null) { //check if spot is empty
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                colsRight++;
            } else {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }
        //check Left
        int colsLeft = 1;
        while (true) {
            if (colPosition - colsLeft < 1) break; //check if OOB
            ChessPosition newLeftPosition = new ChessPosition(rowPosition, colPosition - colsLeft);
            if (board.getPiece(newLeftPosition) == null) { //check if spot is empty
                possibleMoves.add(new ChessMove(myPosition, newLeftPosition, null));
                colsLeft++;
            } else {
                if (board.getPiece(newLeftPosition).getTeamColor() != this.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, newLeftPosition, null));
                }
                break;
            }

        }
        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rowPosition = myPosition.getRow();
        int colPosition = myPosition.getColumn();
        int spacesNE = 1;
        while (true){
            if (rowPosition + spacesNE > 8 || colPosition + spacesNE > 8) break; //check OOB
            ChessPosition newNEPosition = new ChessPosition(rowPosition + spacesNE, colPosition + spacesNE);
            if (board.getPiece(newNEPosition) == null){
                possibleMoves.add(new ChessMove(myPosition,newNEPosition, null));
                spacesNE++;
            } else {
                if (board.getPiece(newNEPosition).getTeamColor() != this.getTeamColor()){
                    possibleMoves.add(new ChessMove(myPosition, newNEPosition, null));
                }
                break;
            }
        }
        //up to the left
        int spacesNW = 1;
        while (true){
            if (rowPosition + spacesNW > 8 || colPosition - spacesNW < 1) break; //check OOB
            ChessPosition newNWPosition = new ChessPosition(rowPosition + spacesNW, colPosition - spacesNW);
            if (board.getPiece(newNWPosition) == null){
                possibleMoves.add(new ChessMove(myPosition,newNWPosition, null));
                spacesNW++;
            } else {
                if (board.getPiece(newNWPosition).getTeamColor() != this.getTeamColor()){
                    possibleMoves.add(new ChessMove(myPosition, newNWPosition, null));
                }
                break;
            }
        }
        //down to the left
        int spacesSW = 1;
        while (true){
            if (rowPosition - spacesSW < 1 || colPosition - spacesSW < 1) break; //check OOB
            ChessPosition newSWPosition = new ChessPosition(rowPosition - spacesSW, colPosition - spacesSW);
            if (board.getPiece(newSWPosition) == null){
                possibleMoves.add(new ChessMove(myPosition,newSWPosition, null));
                spacesSW++;
            } else {
                if (board.getPiece(newSWPosition).getTeamColor() != this.getTeamColor()){
                    possibleMoves.add(new ChessMove(myPosition, newSWPosition, null));
                }
                break;
            }
        }
        //down to the right
        int spacesSE = 1;
        while (true){
            if (rowPosition - spacesSE < 1 || colPosition + spacesSE > 8) break; //check OOB
            ChessPosition newSEPosition = new ChessPosition(rowPosition - spacesSE, colPosition + spacesSE);
            if (board.getPiece(newSEPosition) == null){
                possibleMoves.add(new ChessMove(myPosition,newSEPosition, null));
                spacesSE++;
            } else {
                if (board.getPiece(newSEPosition).getTeamColor() != this.getTeamColor()){
                    possibleMoves.add(new ChessMove(myPosition, newSEPosition, null));
                }
                break;
            }
        }
        return possibleMoves;
    }

}
