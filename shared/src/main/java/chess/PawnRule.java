package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        int rowPosition = pos.getRow();
        int colPosition = pos.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int homeRow;
        int movement;
        int promotionRow;
        ChessPiece.PieceType[] promotionPieces = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};
        int[] attackCol = {-1, 1};
        if (board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.BLACK) {
            homeRow = 7;
            movement = -1;
            promotionRow = 1;
        } else {
            homeRow = 2;
            movement = 1;
            promotionRow = 8;
        }
        if (rowPosition == homeRow) { //home row cases (2 spaces only)
            if (board.getPiece(new ChessPosition(rowPosition + movement, colPosition)) == null &&
                    board.getPiece(new ChessPosition(rowPosition + movement * 2, colPosition)) == null) {
                possibleMoves.add(new ChessMove(pos, new ChessPosition(rowPosition + movement * 2, colPosition), null));
            }
        }
        // move forward
        if (board.getPiece(new ChessPosition(rowPosition + movement, colPosition)) == null) {
            if (rowPosition != promotionRow - movement) { //if it won't lead to promotion
                possibleMoves.add(new ChessMove(pos, new ChessPosition(rowPosition + movement, colPosition), null));
            } else {
                for (ChessPiece.PieceType piece : promotionPieces) {
                    possibleMoves.add(new ChessMove(pos, new ChessPosition(rowPosition + movement, colPosition), piece));
                }
            }
        }
        //capture piece if diagonal
        for (int direction : attackCol) { //check both ways
            if (colPosition + direction >= 1 && colPosition + direction <= 8) { //make sure not OOB
                ChessPosition attackPosition = new ChessPosition(rowPosition + movement, colPosition + direction);
                if (board.getPiece(attackPosition) != null && board.getPiece(attackPosition).getTeamColor() != board.getPiece(pos).getTeamColor()) { //see if it can capture
                    if (rowPosition != promotionRow - movement) { // if not the last row
                        possibleMoves.add(new ChessMove(pos, attackPosition, null));
                    } else {
                        for (ChessPiece.PieceType piece : promotionPieces) {
                            possibleMoves.add(new ChessMove(pos, attackPosition, piece));
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }
}
