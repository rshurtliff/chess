package client;

import chess.*;

import static client.RenderBoard.*;

public class ClientMain {

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var game = new ChessGame().getBoard();
        printBoardForWhite(game);
        printBoardForBlack(game);
        System.out.print(new ChessGame().getBoard());
    }


}
