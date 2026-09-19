package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] kingCoordinates = {{1, 0}, {0, 1}, {0, -1}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        calculateMoves(board, pos, kingCoordinates, possibleMoves, false);
        return possibleMoves;
    }
}