package qirkat;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static qirkat.Move.move;

/**
 * Tests of the Board class.
 *
 * @author QIUCHEN GUO
 */
public class BoardTest {

    private static final String INIT_BOARD =
            "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private static final String[] GAME1 = {"c2-c3", "c4-c2", "c1-c3",
        "a3-c1", "c3-a3", "c5-c4", "a3-c5-c3", };

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
        b0.setPieces("  b b - b b b - - b "
                + "b- - - w ww - - w wwwbww", PieceColor.WHITE);
        assertEquals(true, b0.jumpPossible());

        b0.setPieces("  b b b b b b b b b "
                + "b w - - w ww w w w www-wb", PieceColor.WHITE);
        assertEquals(false, b0.jumpPossible());
    }

    @Test
    public void testgetmoves() {
        Board b0 = new Board();
        b0.setPieces("  b b - b b b - - b "
                + "b- - - b ww - - b wwwbww", PieceColor.WHITE);
        List moves = b0.getMoves();
        assertEquals("[e3-c3, e3-c1, e4-c4, e5-c3]", moves.toString());

        b0 = new Board();
        List move2 = b0.getMoves();
        assertEquals("[b2-c3, c2-c3, d2-c3, d3-c3]", move2.toString());

        b0.clear();
        b0.setPieces("  - - - - - - b - - - - "
                + "- - - w - - - - - - - - - -", PieceColor.WHITE);
        List move3 = b0.getMoves();
        assertEquals("[e3-d3, e3-e4, e3-d4]", move3.toString());

        b0.clear();
        b0.setPieces("  - - - - - - - - - - - b"
                + " w - - b b - - - - - - - -", PieceColor.WHITE);
        List move4 = b0.getMoves();
        assertEquals("[c3-a3-a5-c3, c3-a3-c5, c3-a5-a3-c3]", move4.toString());

        b0.clear();
        b0.setPieces("----- -w--- -bbb- ----- -----", PieceColor.WHITE);
        Move move5 = move('b', '2', 'b', '4', move('b', '4', 'd', '2'));
        assertEquals(false, b0.checkJump(move5));

        b0.clear();
        b0.setPieces("----- -w--- -bbb- ----- -----", PieceColor.WHITE);
        Move move6 = move('b', '2', 'b', '4',
                move('b', '4', 'd', '2', move('d', '2', 'd', '4')));
        Boolean legal = b0.checkJump(move6);
        assertEquals(true, legal);

    }


    @Test
    public void testmakemove() {
        Board b = new Board();
        b.setPieces("  - - - - - - b - - - - "
                + "- - - w - - - - - - - - - -", PieceColor.WHITE);
        b.makeMove(move('e', '3', 'd', '3'));
        b.makeMove(move('b', '2', 'a', '2'));
        assertEquals(false, b.legalMove(move('d', '3', 'e', '3')));
        assertEquals(true, b.legalMove(move('d', '3', 'c', '3')));
        boolean legal = b.legalMove(move('a', '2', 'b', '2'));
        assertEquals(false, legal);

        b.clear();
        b.setPieces(" b - w - w"
                + "- - - w -"
                + "- - - - w"
                + "- b - - b"
                + "- b b b b", PieceColor.WHITE);
        b.makeMove(move('d', '2', 'e', '2'));
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

    @Test
    public void testgameover() {
        Board b = new Board();
        b.setPieces("w--w- w---- b---- ----- -----", PieceColor.WHITE);
        b.makeMove('a', '2', 'a', '4');
        assertEquals(true, b.gameOver());
    }

    @Test
    public void testblack() {
        Board b = new Board();
        b.setPieces("----w b---- ----- ----- -----", PieceColor.BLACK);
        boolean legal = b.legalMove(move('a', '2', 'b', '2'));
    }
}
