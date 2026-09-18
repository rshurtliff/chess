package chess;

import java.util.Collection;

public abstract class BaseMovementRule implements MovementRule{

    public abstract Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return calculateMoves(board, position);
    }
}
