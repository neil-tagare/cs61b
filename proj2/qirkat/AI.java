package qirkat;

import java.util.ArrayList;

import static qirkat.Move.MAX_INDEX;
import static qirkat.PieceColor.BLACK;
import static qirkat.PieceColor.WHITE;

/**
 * A Player that computes its own moves.
 *
 * @author QIUCHEN GUO
 */
class AI extends Player {

    /**
     * Maximum minimax search depth before going to static evaluation.
     */
    private static final int MAX_DEPTH = 8;
    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI for GAME that will play MYCOLOR.
     */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        game().reportMove(myColor() + " moves " + move.toString() + ".");
        return move;
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        Move best;
        best = null;
        int bestsofar = -INFTY;

        if (depth == 0) {
            return staticScore(board);
        }

        ArrayList<Move> moves = board.getMoves();
        for (Move move : moves) {
            if (sense == 1) {
                bestsofar = minmax(board, move, depth, sense, alpha, beta);
                alpha = Math.max(bestsofar, alpha);
                best = move;
                if (beta <= alpha) {
                    break;
                }
            } else if (sense == -1) {
                bestsofar = minmax(board, move, depth, sense, alpha, beta);
                beta = Math.min(bestsofar, beta);
                best = move;
                if (beta <= alpha) {
                    break;
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }

        return bestsofar;
    }

    /**
     * @param board current board.
     * @param move move.
     * @param depth search depth.
     * @param sense white or black.
     * @param alpha alpha.
     * @param beta beta.
     * @return boardvalue
     * Helper function.
     * */
    private int minmax(Board board, Move move, int depth,
                       int sense, int alpha, int beta) {
        Board b = new Board(board);
        b.makeMove(move);
        if (b.gameOver() && sense == 1) {
            return INFTY;
        } else if (b.gameOver() && sense == -1) {
            return -INFTY;
        }
        int best = findMove(b, depth - 1, false, sense * (-1), alpha, beta);
        return best;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        int wc = 0;
        int bc = 0;
        for (int k = 0; k <= MAX_INDEX; k++) {
            if (board.get(k).isPiece() && board.get(k).equals(WHITE)) {
                wc += 1;
            } else if (board.get(k).isPiece() && board.get(k).equals(BLACK)) {
                bc += 1;
            }
        }
        return wc - bc;
    }
}
