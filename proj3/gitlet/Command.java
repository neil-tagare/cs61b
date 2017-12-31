package gitlet;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** All things to do with parsing commands.
 *  @author QIUCHEN GUO
 */
class Command {

    /** Command types.  PIECEMOVE indicates a move of the form
     *  c0r0-c1r1.  ERROR indicates a parse error in the command.
     *  All other commands are upper-case versions of what the
     *  programmer writes. */
    static enum Type {
        /* Start-up state only. */
        ADD("add"),
        COMMIT("commit"),
        REMOVE("rm"),
        INIT("init"),
        LOG("log"),
        CHECKOUT("checkout"),
        FIND("find"),
        BRANCH("branch"),
        RMBRANCH("rm-branch"),
        RESET("reset"),
        MERGE("merge"),
        GLOBALLOG("global-log"),
        STATUS("status"),
        ADDREMOTE("add-remote"),
        FETCH("fetch"),
        PUSH("push"),
        PULL("pull"),
        RMREMOTE("rm-remote"),
        ERROR(".*"),
        /** End of input stream. */
        EOF;

        /** PATTERN is a regular expression string giving the syntax of
         *  a command of the given type.  It matches the entire command,
         *  assuming no leading or trailing whitespace.  The groups in
         *  the pattern capture the operands (if any). */
        Type(String pattern) {
            _pattern = Pattern.compile(pattern + "$");
        }

        /** A Type whose pattern is the lower-case version of its name. */
        Type() {
            _pattern = Pattern.compile(this.toString().toLowerCase() + "$");
        }

        /** The Pattern descrbing syntactically correct versions of this
         *  type of command. */
        private final Pattern _pattern;

    }

    /** A new Command of type TYPE with OPERANDS as its operands. */
    Command(Type type, String... operands) {
        _type = type;
        _operands = operands;
    }

    /** Return the type of this Command. */
    Type commandType() {
        return _type;
    }

    /** Returns this Command's operands. */
    String[] operands() {
        return _operands;
    }

    /** Parse COMMAND, returning the command and its operands. */
    static Command parseCommand(String[] command) {
        if (command == null) {
            return new Command(Type.EOF);
        }
        for (Type type : Type.values()) {
            Matcher mat = type._pattern.matcher(command[0]);
            if (mat.matches()) {
                String[] operands = new String [command.length - 1];
                for (int i = 1; i < command.length; i += 1) {
                    operands[i - 1] = command[i];
                }
                return new Command(type, operands);
            }
        }
        throw new Error("Internal failure: error command did not match.");
    }

    /** The command name. */
    private final Type _type;
    /** Command arguments. */
    private final String[] _operands;
}
