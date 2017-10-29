package qirkat;

import antlr.ASdebug.ASDebugStream;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/** Tests of the Board class.
 *  @author
 */
public class BoardTest {

    private static final String INIT_BOARD =
        "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private static final String[] GAME1 =
    { "c2-c3", "c4-c2",
      "c1-c3", "a3-c1",
      "c3-a3", "c5-c4",
      "a3-c5-c3",
    };

    private static final String GAME1_BOARD =
        "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }

    @Test
    public void testInit1() {
        Board b0 = new Board();
        assertEquals(INIT_BOARD, b0.toString());
    }

    @Test
    public void testJumppossible() {
        Board b0 = new Board();
        b0.setPieces("  b b - b b b - - b b- - - w ww - - w wwwbww", PieceColor.BLACK);
        assertEquals(true, b0.jumpPossible());

        b0.setPieces("  b b b b b b b b b b w - - w ww w w w www-wb", PieceColor.BLACK);
        assertEquals(false, b0.jumpPossible());
    }

    @Test
    public void testgetmoves() {
        Board b0 = new Board();
//        b0.setPieces("  b b - b b b - - b b- - - b ww - - b wwwbww", PieceColor.BLACK);
//        List moves = b0.getMoves();
//        assertEquals("[e3-c3, e3-c1, e4-c4, e5-c3]", moves.toString());
//
//        b0 = new Board();
//        List move2 = b0.getMoves();
//        assertEquals("[b2-c3, c2-c3, d2-c3, d3-c3]", move2.toString());
//
//        b0.setPieces("  - - - - - - b - - - - - - - w - - - - - - - - - -", PieceColor.BLACK);
//        List move3 = b0.getMoves();
//        assertEquals("[e3-d3, e3-e4, e3-d4]", move3.toString());

        b0.setPieces("  - - - - - - - - - - - b w - - - b - - - - - - - -", PieceColor.BLACK);
//        System.out.println(b0.toString());
        List move4 = b0.getMoves();
        System.out.println(move4);
//        assertEquals("[e3-c3, e3-c1, e4-c4, e5-c3]", moves.toString());
    }


    @Test
    public void testMoves1() {
        Board b0 = new Board();
        makeMoves(b0, GAME1);
        assertEquals(GAME1_BOARD, b0.toString());
    }

    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }

        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position", b2, b0);
    }

}
