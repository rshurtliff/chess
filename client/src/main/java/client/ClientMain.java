package client;

import chess.*;

import static client.RenderBoard.*;

public class ClientMain {

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        printBoard(new ChessGame().getBoard());
        System.out.print(new ChessGame().getBoard());
    }


}
