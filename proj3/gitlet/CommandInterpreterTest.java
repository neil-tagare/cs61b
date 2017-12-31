package gitlet;

import org.junit.Test;
import ucb.junit.textui;

/**
 * The suite of all JUnit tests for the gitlet package.
 *
 * @author
 */
public class CommandInterpreterTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * A dummy test to avoid complaint.
     */
    @Test
    public void testdoAdd() {
        String[] s = new String[2];
        s[0] = "add";
        s[1] = "wug.txt";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoInit() {
        String[] s = new String[1];
        s[0] = "sth";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        String[] operands = new String[1];
        c.doInit(operands);
    }

    @Test
    public void testdocommit() {
        String[] s = new String[1];
        s[0] = "commit";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        String[] operands = new String[1];
        operands[0] = "add two files";
        c.doCommit(operands);
    }

    @Test
    public void testdoRemove() {
        String[] s = new String[2];
        s[0] = "rm";
        s[1] = "testing/src/wug.txt";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoLog() {
        String[] s = new String[1];
        s[0] = "log";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoCheckout1() {
        String[] s = new String[3];
        s[0] = "checkout";
        s[1] = "--";
        s[2] = "testing/src/wug.txt";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoCheckout2() {
        String[] s = new String[4];
        s[0] = "checkout";
        s[1] = "c4be87";
        s[2] = "--";
        s[3] = "testing/src/wug.txt";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoBranch() {
        String[] s = new String[2];
        s[0] = "branch";
        s[1] = "R1/master";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoRemoveBranch() {
        String[] s = new String[2];
        s[0] = "rm-branch";
        s[1] = "other";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoMerge() {
        String[] s = new String[2];
        s[0] = "merge";
        s[1] = "other";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoStatus() {
        String[] s = new String[1];
        s[0] = "status";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }

    @Test
    public void testdoReset() {
        String[] s = new String[2];
        s[0] = "reset";
        s[1] = "c8c048a";
        CommandInterpreter c = new CommandInterpreter(s, System.out);
        c.process();
    }
}

