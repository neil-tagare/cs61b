package qirkat;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static qirkat.GameException.error;
import static qirkat.PieceColor.BLACK;
import static qirkat.PieceColor.WHITE;

/**
 * @author qiuchen guo
 */
public class Gametest {
    /**
     * test the dodump method.
     */
    @Test
    public void testdodump() {
        Reporter re = new TextReporter();
        Board b = new Board();
        re.moveMsg("%s", "===");
        re.moveMsg(b.toString());
        re.moveMsg("%s\n", "===");
    }

    /**
     * test the set method.
     */
    @Test
    public void testdoset() {
        Board b = new Board();
        String[] operands = {"white", "----- -w--- ----- ----- ----b"};
        String wm = (operands[0]);
        PieceColor whosemove = null;
        if (wm.toUpperCase().equals("WHITE")) {
            whosemove = WHITE;
        } else if (wm.toUpperCase().equals("BLACK")) {
            whosemove = BLACK;
        }
        String s = operands[1];
        b.setPieces(s, whosemove);
    }

    /**
     * test the move method.
     */
    @Test
    public void testdomove() {
        Board b = new Board();
        String[] operands = {"white", "----- -w--- -bbb- ----- -----"};
        String wm = (operands[0]);
        PieceColor whosemove = null;
        if (wm.toUpperCase().equals("WHITE")) {
            whosemove = WHITE;
        } else if (wm.toUpperCase().equals("BLACK")) {
            whosemove = BLACK;
        }
        String s = operands[1];
        b.setPieces(s, whosemove);

        String[] operand = {"b2-b4-d2-d4"};
        String s1 = operand[0];
        Move move = Move.parseMove(s1);
        if (b.legalMove(move)) {
            if (!move.isJump() || (move.isJump() && b.checkJump(move))) {
                b.makeMove(move);
            } else {
                System.out.println("wrong");
            }
        } else {
            System.out.println("wrong");
        }

        System.out.println(b.toString());
    }

    /**
     * test doload method.
     * */
    @Test
    public void testdoload() {
        try {
            File file = new File("input.txt");
            FileInputStream in = new
                    FileInputStream("C:\\Users\\TAFLAB\\Dropbox\\"
                    + "Course\\CS61B\\repo\\proj2\\testing\\Input");
            InputStreamReader reader = new InputStreamReader(in);
            ReaderSource filesource = new ReaderSource(reader, true);
        } catch (IOException e) {
            throw error("Cannot open file %s", "input");
        }
    }
}
