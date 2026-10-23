package chess;

public class TestingSandbox {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessPiece piece = board.getPiece(new ChessPosition(1,1));
        System.out.println(board);
        System.out.println("Piece at A1:");
        System.out.println(piece);
        System.out.println(piece.getTeamColor());


    }

}
