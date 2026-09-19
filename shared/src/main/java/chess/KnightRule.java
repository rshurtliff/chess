package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] knightCoordinates = {{2,1}, {1,2}, {-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
            calculateMoves(board,pos,knightCoordinates,possibleMoves,false);
        return possibleMoves;
    }
}

