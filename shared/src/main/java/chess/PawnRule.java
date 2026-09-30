package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int rowPosition = pos.getRow();
        int colPosition = pos.getColumn();
        int homeRow;
        int movement;
        int promotionRow;

        if (board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.BLACK) {
            homeRow = 7;
            movement = -1;
            promotionRow = 1;
        } else {
            homeRow = 2;
            movement = 1;
            promotionRow = 8;
        }
        boolean promotion = (promotionRow == rowPosition + movement);

        //initial move
        if (rowPosition == homeRow) { //home row cases (2 spaces only)
            if (board.getPiece(new ChessPosition(rowPosition + movement, colPosition)) == null &&
                    board.getPiece(new ChessPosition(rowPosition + movement * 2, colPosition)) == null) {
                possibleMoves.add(new ChessMove(pos, new ChessPosition(rowPosition + movement * 2, colPosition), null));
            }
        }
        // move forward (don't have to check forward boundary because a pawn will never be in rows 1 or 8.
        if (board.getPiece(new ChessPosition(rowPosition + movement, colPosition)) == null) {
            ChessPosition newPos = new ChessPosition(rowPosition + movement, colPosition);
            addPawnMoves(promotion, possibleMoves, pos, newPos);
        }
        //capture
        int[] attackCol = {-1, 1};
        for (int dir : attackCol) {
            int newCol = colPosition + dir;
            if (newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(rowPosition + movement, newCol);
                if (board.getPiece(newPos) != null && board.getPiece(newPos).getTeamColor() != board.getPiece(pos).getTeamColor()) {
                    addPawnMoves(promotion, possibleMoves, pos, newPos);
                }
            }
        }
        return possibleMoves;
    }

    private void addPawnMoves(boolean promotion, Collection<ChessMove> possibleMoves, ChessPosition pos, ChessPosition newPos) {
        ChessPiece.PieceType[] promotionPieces = {ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};
        if (!promotion) {
            possibleMoves.add(new ChessMove(pos, newPos, null));
        } else {
            for (ChessPiece.PieceType piece : promotionPieces) {
                possibleMoves.add(new ChessMove(pos, newPos, piece));
            }
        }
    }
}
