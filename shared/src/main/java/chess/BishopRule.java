package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] bishopDirections = {{1,1},{-1,-1},{1,-1},{-1,1}};
        calculateMoves(board, pos, bishopDirections, possibleMoves, true);
        return possibleMoves;
    }
}
