package gitlet;

import static gitlet.Utils.join;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  This gitlet system is developed by
 *  @author Qiuchen Guo given valueble discussions with Duy Nguyen in cs61b.
 *  The skeleton code of command interpreter from Paul Hilfinger
 *  is used.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {

        /**
         * Interpret input commands
         * */
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        } else if (!join(System.getProperty("user.dir"),
                ".gitlet").exists() && !args[0].equals("init")) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        CommandInterpreter interpreter =
                new CommandInterpreter(args, System.out);
        interpreter.process();

    }
}
