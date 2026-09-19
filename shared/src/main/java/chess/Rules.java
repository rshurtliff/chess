package chess;

import java.util.HashMap;

public class Rules{
    private final HashMap<ChessPiece.PieceType, MovementRule> rules = new HashMap<>();

    public Rules(){
        rules.put(ChessPiece.PieceType.KNIGHT, new KnightRule());
        rules.put(ChessPiece.PieceType.KING, new KingRule());
        rules.put(ChessPiece.PieceType.QUEEN, new QueenRule());
        rules.put(ChessPiece.PieceType.ROOK, new RookRule());
        rules.put(ChessPiece.PieceType.BISHOP, new BishopRule());
        rules.put(ChessPiece.PieceType.PAWN, new PawnRule());
    }

    public MovementRule getPieceRule(ChessPiece.PieceType type){
        return rules.get(type);
    }
}
