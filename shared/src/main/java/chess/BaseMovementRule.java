package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseMovementRule implements MovementRule{
    protected void calculateMoves(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove,
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
                if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){ //if it's occupied, check to see if you can capture it
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
            if (!allowDistance){break;} //just do this once if it's not a distance move
        }
    }
    public abstract Collection<ChessMove> pieceMoves (ChessBoard board, ChessPosition pos);
}
