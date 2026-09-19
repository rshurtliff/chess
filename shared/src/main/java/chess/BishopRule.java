package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] bishopDirections = {{1,1},{-1,-1},{1,-1},{-1,1}};
        for (int[] move : bishopDirections) {
            int row = move[0];
            int col = move[1];
            calculateMoves(board, pos, row, col, possibleMoves, true);
        }
        return possibleMoves;
    }
}
