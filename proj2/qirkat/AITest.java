package qirkat;

import org.junit.Test;

import java.util.ArrayList;

import static qirkat.Move.MAX_INDEX;
import static qirkat.PieceColor.BLACK;
import static qirkat.PieceColor.WHITE;

public class AITest {

    @Test
    public void testminmax() {
        PieceColor myColor = WHITE;
        Move move = findMove(myColor);
        System.out.println(move.toString());
    }


    private Move findMove(PieceColor myColor) {
        Board b = new Board();
        b.setPieces("----w -w-ww b---w ----- -bbbb", PieceColor.BLACK);
        if (myColor == WHITE) {
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
    static final int MAX_DEPTH = 8;
    private static final int INFTY = Integer.MAX_VALUE;

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
        System.out.println(moves.toString());
        for (Move move : moves) {
            if (sense == 1.0) {
                bestsofar = minmax(board, move, depth, sense, alpha, beta);
                alpha = Math.max(bestsofar, alpha);
                best = move;
                if (beta <= alpha) {
                    break;
                }
            } else if (sense == -1.0) {
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

    private int minmax(Board board, Move move, int depth,
                       int sense, int alpha, int beta) {
        Board b = new Board(board);
        b.makeMove(move);
        if (b.gameOver() && sense == 1) {
            return INFTY;
        } else if (b.gameOver() && sense == -1) {
            System.out.println(b.toString());
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
