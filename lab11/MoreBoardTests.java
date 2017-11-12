package qirkat;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MoreBoardTests {

    // the string representation of this is
    // "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w"
    // feel free to modify this to however you want to represent your board.
    private final char[][] boardRepr = new char[][]{
            {'b', 'b', 'b', 'b', 'b'},
            {'b', 'b', 'b', 'b', 'b'},
            {'b', 'b', '-', 'w', 'w'},
            {'w', 'w', 'w', 'w', 'w'},
            {'w', 'w', 'w', 'w', 'w'}
    };

    private final PieceColor currMove = PieceColor.WHITE;

    /**
     * @return the String representation of the initial state. This will
     * be a string in which we concatenate the values from the bottom of
     * board upwards, so we can pass it into setPieces. Read the comments
     * in Board#setPieces for more information.
     * <p>
     * For our current boardRepr, the String returned by getInitialRep
     * resentation is
     * "  w w w w w\n  w w w w w\n  b b - w w\n  b b b b b\n  b b b b b"
     * <p>
     * We use a StringBuilder to avoid recreating Strings (because Strings
     * are immutable).
     */
    private String getInitialRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = boardRepr.length - 1; i >= 0; i--) {
            for (int j = 0; j < boardRepr[0].length; j++) {
                sb.append(boardRepr[i][j] + " ");
            }
            sb.deleteCharAt(sb.length() - 1);
            if (i != 0) {
                sb.append("\n  ");
            }
        }
        return sb.toString();
    }

    // create a new board with the initial state.
    private Board getBoard() {
        Board b = new Board();
        b.setPieces(getInitialRepresentation(), currMove);
        return b;
    }

    // reset board b to initial state.
    private void resetToInitialState(Board b) {
        b.setPieces(getInitialRepresentation(), currMove);
    }

    @Test
    public void testSomething() {
        Board b = getBoard();
        b.setPieces("  b b - b b b - - b "
                + "b- - - w ww - - w wwwbww", PieceColor.BLACK);
        assertEquals(true, b.jumpPossible());

        b.setPieces("  b b b b b b b b b "
                + "b w - - w ww w w w www-wb", PieceColor.BLACK);
        assertEquals(false, b.jumpPossible());

        Board b0 = getBoard();
        b0.setPieces("  b b - b b b - - b "
                + "b- - - b ww - - b wwwbww", PieceColor.BLACK);
        List moves = b0.getMoves();
        assertEquals("[e3-c3, e3-c1, e4-c4, e5-c3]", moves.toString());

        b0 = new Board();
        List move2 = b0.getMoves();
        assertEquals("[b2-c3, c2-c3, d2-c3, d3-c3]", move2.toString());

        b0.setPieces("  - - - - - - b - - - - "
                + "- - - w - - - - - - - - - -", PieceColor.BLACK);
        List move3 = b0.getMoves();
        assertEquals("[e3-d3, e3-e4, e3-d4]", move3.toString());

        b0.setPieces("  - - - - - - - - - - - b"
                + " w - - b b - - - - - - - -", PieceColor.BLACK);
        List move4 = b0.getMoves();
        assertEquals("[c3-a3-a5-c3, c3-a3-c5, c3-a5-a3-c3]", move4.toString());
    }
}

