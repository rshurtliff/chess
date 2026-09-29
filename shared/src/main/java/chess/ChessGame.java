package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {
        turn = TeamColor.WHITE;
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = gameBoard.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = piece.pieceMoves(gameBoard,startPosition);
        for(ChessMove move: possibleMoves){
            ChessBoard testBoard = new ChessBoard(gameBoard);
            testBoard.movePieceHelper(move);
            if(!isInCheck(piece.getTeamColor(), testBoard)){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //gotta make sure the move doesnt try to be from an empty square, or you'll get a null ptr exception
        //make sure it's your turn too
        //this should also change the turn to be the opposite
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        if(piece == null){throw new InvalidMoveException();}
        TeamColor color = piece.getTeamColor();
        if(!validMoves(move.getStartPosition()).contains(move) || getTeamTurn() != color){
            throw new InvalidMoveException();
        }
        gameBoard.movePieceHelper(move);
        if(color == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, gameBoard);
    }
    private boolean isInCheck(TeamColor teamColor, ChessBoard board){
        ChessPosition kingPos = board.findKing(teamColor);
        if (kingPos == null){return false;}
        TeamColor oppColor = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
        Collection<ChessPosition> opponentPositions = board.findAllPieces(oppColor);
        for (ChessPosition oppPos : opponentPositions){
            ChessPiece oppPiece = board.getPiece(oppPos);
            Collection<ChessMove> possibleMoves = oppPiece.pieceMoves(board,oppPos);
            for (ChessMove move : possibleMoves){
                if (move.getEndPosition().equals(kingPos)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
