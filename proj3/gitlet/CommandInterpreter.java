package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import static gitlet.Paths.*;
import static gitlet.Command.Type.*;
import static gitlet.Utils.*;

/**
 * An object that reads and interprets a sequence of commands from an
 * input source.
 *
 * @author Qiuchen Guo
 */
class CommandInterpreter {
    /**
     * The command input source.
     */
    private String[] _inputs;
    /**
     * The hashmap maps command type to method.
     */
    private final HashMap<Command.Type, Consumer<String[]>> _commands =
            new HashMap<>();

    {
        _commands.put(INIT, this::doInit);
        _commands.put(ADD, this::doAdd);
        _commands.put(COMMIT, this::doCommit);
        _commands.put(REMOVE, this::doRemove);
        _commands.put(LOG, this::doLog);
        _commands.put(FIND, this::doFind);
        _commands.put(CHECKOUT, this::doCheckout);
        _commands.put(BRANCH, this::doBranch);
        _commands.put(RMBRANCH, this::doRMbranch);
        _commands.put(GLOBALLOG, this::doGloballog);
        _commands.put(STATUS, this::doStatus);
        _commands.put(RESET, this::doReset);
        _commands.put(MERGE, this::doMerge);
        _commands.put(ERROR, this::doError);
        _commands.put(ADDREMOTE, this::doAddremote);
        _commands.put(PUSH, this::doPush);
        _commands.put(PULL, this::doPull);
        _commands.put(RMREMOTE, this::doRMremote);
        _commands.put(FETCH, this::doFetch);
    }

    /**
     * A new CommandInterpreter executing commands read from INP, writing
     * prompts on PROMPTER, if it is non-null.
     */
    CommandInterpreter(String[] inp, PrintStream prompter) {
        _inputs = inp;
    }

    /**
     * process() method.
     */
    void process() {
        doCommand();
    }

    /**
     * Perform the next command from our input source.
     */
    void doCommand() {
        try {
            Command cmnd =
                    Command.parseCommand(_inputs);
            _commands.get(cmnd.commandType()).accept(cmnd.operands());
        } catch (GitletException e) {
            System.out.printf("Error: %s%n", e.getMessage());
        }
    }

    /**
     * Perform the command 'doInit OPERANDS[0]'.
     */
    void doInit(String[] unused) {
        File theDir = new File(".gitlet");
        if (!theDir.exists()) {
            theDir.mkdir();
            new File(".gitlet/stage").mkdir();
            new File(".gitlet/objects").mkdir();
            new File(".gitlet/refs").mkdir();
            new File(".gitlet/refs/heads").mkdir();

            Commit commit = new Commit("initial commit", "null",
                    new Date(Instant.EPOCH.getEpochSecond()));

            String branch = "master";
            String path = ".gitlet/objects/";
            writeObject(join(path, commit.sha1()), commit);
            writeContents(join(".gitlet/HEAD"), branch.getBytes());
            writeObject(join(".gitlet/refs/heads/master"), commit);
            writeObject(join(".gitlet/delete"), new Delete());

            Stage s = new Stage();
            writeObject(join(".gitlet/stage/index"), s);
        } else {
            System.out.println("A Gitlet version-control system"
                    + " already exists in the current directory.");
        }
    }

    /**
     * Perform the command 'Add OPERANDS[0]'.
     */
    void doAdd(String... operands) {
        Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
        File file = new File(operands[0]);
        Blob b;
        Delete d = readObject(join(".gitlet/delete"), Delete.class);
        if (file.exists()) {
            if (changenotstaged(operands[0])
                    | trackedchanged(operands[0]) | !tracked(operands[0])) {
                b = new Blob(file);
                String path = ".gitlet/stage/";
                writeObject(join(path, b.getname()), b);
                s.put(operands[0], b.getname());
            }
        } else {
            System.out.println("File does not exist.");
        }

        if (tracked(operands[0]) && d.getfile().contains(operands[0])) {
            d.remove(operands[0]);
            s.remove(operands[0]);
        }


        writeObject(join(".gitlet/stage/index"), s);
        writeObject(join(".gitlet/delete"), d);
    }


    /**
     * Perform the command 'Add OPERANDS[0]'.
     */
    @SuppressWarnings("unchecked")
    void doCommit(String... operands) {
        if (operands == null || operands[0].equals("")) {
            System.out.println("Please enter a commit message.");
        }

        boolean changed = false;
        String message = operands[0];
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit parent = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        Commit parent2 = null;
        Commit commit = new Commit(message, parent.getsha1());
        if (message.split(" ")[0].equals("Merged")) {
            parent2 = readObject(join(".gitlet/refs/heads",
                    message.split(" ")[1]), Commit.class);
            commit = new Commit(message, parent.getsha1(), parent2.getsha1());
        }
        for (String name : (Set<String>) parent.getfile().keySet()) {
            commit.put(name, (String) parent.getfile().get(name));
        }

        Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
        Set<String> keys = s.getfile().keySet();
        for (String name : keys) {
            changed = true;
            String sha1 = (String) s.getfile().get(name);
            Blob b = readObject(join(".gitlet/stage", sha1), Blob.class);
            commit.put(name, sha1);
            writeObject(join(".gitlet/objects", sha1), b);
        }

        Delete d = readObject(join(".gitlet/delete"), Delete.class);
        for (String todelete : d.getfile()) {
            changed = true;
            if (commit.getfile().get(todelete) != null) {
                commit.remove(todelete);
            }
        }
        d = new Delete();

        if (!changed) {
            System.out.println("No changes added to the commit.");
            return;
        }

        writeObject(join(".gitlet/objects", commit.sha1()), commit);
        writeObject(join(".gitlet/refs/heads", head), commit);
        writeObject(join(".gitlet/delete"), d);
        for (String f : plainFilenamesIn(".gitlet/stage")) {
            join(".gitlet/stage", f).delete();
        }
        writeObject(join(".gitlet/stage/index"), new Stage());
    }

    /**
     * Perform the command 'Remove OPERANDS[0]'.
     */
    void doRemove(String... operands) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        File record = new File(".gitlet/delete");
        Delete d = readObject(record, Delete.class);
        boolean find = false;
        for (String f : operands) {
            if (current.getfile().get(f) != null) {
                d.add(f);
                if (join(f).exists()) {
                    join(f).delete();
                }
                find = true;
            }

            if (join(".gitlet/stage/index").exists()) {
                Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
                if (s.getfile().get(f) != null) {
                    s.remove(f);
                    writeObject(join(".gitlet/stage/index"), s);
                    find = true;
                    break;
                }
            }
        }

        writeObject(join(".gitlet/delete"), d);

        if (!find) {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * Perform the command 'log'.
     */
    void doLog(String[] unused) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        String path = ".gitlet/objects";
        String sha1 = current.getsha1();
        while (join(path, sha1).exists()) {
            Commit commit = readObject(join(path, sha1), Commit.class);
            commit.print();
            sha1 = commit.parent();
        }
    }

    /**
     * Perform the command 'global-log'.
     */
    void doGloballog(String[] unused) {
        String path = ".gitlet/objects";
        List<String> all = plainFilenamesIn(path);
        for (String c : all) {
            if (c.charAt(0) == 'c') {
                Commit commit = readObject(join(path, c), Commit.class);
                commit.print();
            }
        }
    }

    /**
     * Perform the command 'Find OPERANDS[0]'.
     */
    void doFind(String[] operands) {
        String path = ".gitlet/objects";
        boolean find = false;
        for (String sha1 : plainFilenamesIn(path)) {
            if (join(path, sha1).exists() && sha1.charAt(0) == 'c') {
                Commit commit = readObject(join(path, sha1), Commit.class);
                if (commit.log().equals(operands[0])) {
                    find = true;
                    System.out.println(sha1);
                }
            }
        }
        if (!find) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Perform the command 'status'.
     */
    @SuppressWarnings("unchecked")
    void doStatus(String[] unused) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        System.out.println("=== Branches ===");
        List<String> branches = plainFilenamesIn(".gitlet/refs/heads");
        Collections.sort(branches);
        for (String branch : branches) {
            if (head.equals(branch)) {
                System.out.format("*%s\n", branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();


        Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
        List<String> keys = new ArrayList<String>(s.getfile().keySet());
        Collections.sort(keys);
        System.out.println("=== Staged Files ===");
        for (String name : keys) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Delete d = readObject(join(".gitlet/delete"), Delete.class);
        for (String todelete : d.getfile()) {
            System.out.println(todelete);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String f : plainFilenamesIn(System.getProperty("user.dir"))) {
            if (trackedchanged(f) && s.getfile().get(f) == null) {
                System.out.format("%s (modified)\n", f);
            } else if (changenotstaged(f)) {
                System.out.format("%s (modified)\n", f);
            }
        }

        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        for (String f : (Set<String>) current.getfile().keySet()) {
            if (s.getfile().get(f) != null && !join(f).exists()) {
                System.out.format("%s (deleted)\n", f);
            } else if (!join(f).exists() && trackeddeleted(f)
                    && !d.getfile().contains(f)) {
                System.out.format("%s (deleted)\n", f);
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String f : plainFilenamesIn(System.getProperty("user.dir"))) {
            if (!tracked(f) && !staged(f)) {
                System.out.format("%s\n", f);
            }
        }
        System.out.println();
    }

    /**
     * @param name is filename
     * @return true if the file is tracked
     * */
    boolean tracked(String name) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        if (current.getfile().get(name) != null) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is staged
     * */
    boolean staged(String name) {
        Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
        if (s.getfile().get(name) != null) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is tracked but changed
     * */
    boolean trackedchanged(String name) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        String newsha1 = "b" + sha1(readContentsAsString(join(name)));
        if (current.getfile().get(name) != null
                && !current.getfile().get(name).equals(newsha1)) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is tracked but changed
     * */
    boolean trackeddeleted(String name) {
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        if (current.getfile().get(name) != null
                && !plainFilenamesIn(System.getProperty("user.dir"))
                .contains(name)) {
            return true;
        }
        return false;
    }

    /**
     * @param name is filename
     * @return true if the file is staged but changed
     * */
    boolean changenotstaged(String name) {
        Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
        String newsha1 = "b" + sha1(readContentsAsString(join(name)));
        if (s.getfile().get(name) != null
                && !s.getfile().get(name).equals(newsha1)) {
            return true;
        }
        return false;

    }

    /**
     * @param operands is input args.
     *                 Perform the command 'checkout -- file'.
     *                 Perform the command 'checkout commit -- file'.
     */
    @SuppressWarnings("unchecked")
    void doCheckout(String[] operands) {
        if (operands[0].equals("--")) {
            String name = operands[1];
            String head = readContentsAsString(join(".gitlet/HEAD"));
            Commit current = readObject(join(".gitlet/refs/heads", head),
                    Commit.class);
            String blobsha1 = (String) current.getfile().get(name);
            if (blobsha1 != null) {
                Blob b = readObject(join(".gitlet/objects", blobsha1),
                        Blob.class);
                writeContents(join(name), b.content());
                return;
            } else {
                System.out.println("File does not exist in that commit.");
            }

        } else if (operands.length == 3) {
            if (operands[1].equals("--")) {
                String commitid = operands[0];
                String name = operands[2];
                String path = ".gitlet/objects";
                Boolean exsitid = false;
                for (String sha1 : plainFilenamesIn(path)) {
                    if (sha1.charAt(0) == 'c') {
                        Commit commit = readObject(join(path, sha1),
                                Commit.class);
                        if (commitid.regionMatches(true, 0,
                                sha1, 0, commitid.length())) {
                            exsitid = true;
                            if (commit.getfile().get(name) != null) {
                                String blobsha1 = (String)
                                        commit.getfile().get(name);
                                Blob b = readObject(join(".gitlet/objects",
                                        blobsha1),
                                        Blob.class);
                                writeContents(join(name), b.content());
                                return;
                            }
                        }
                    }
                }

                if (exsitid) {
                    System.out.println("File does not exist in that commit.");
                } else {
                    System.out.println("No commit with that id exists.");
                }
            } else {
                System.out.println("Incorrect operands.");
            }
        } else {
            doCheckout(operands[0]);
        }
    }

    /**
     * @param operands is input args.
     *                 perform the command 'checkout branch'
     */
    @SuppressWarnings("unchecked")
    void doCheckout(String operands) {
        String currentbranch = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads",
                currentbranch),
                Commit.class);
        String givenbranch = operands;
        if (givenbranch.equals(currentbranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }


        String path = ".gitlet/";
        if (join(".gitlet/refs/heads", givenbranch).exists()) {
            Commit given = readObject(join(".gitlet/refs/heads",
                    givenbranch),
                    Commit.class);
            Set<String> keys = given.getfile().keySet();
            Set<String> keyscurrent = current.getfile().keySet();

            for (String f : plainFilenamesIn(System.getProperty
                    ("user.dir"))) {
                if (!keyscurrent.contains(f) && keys.contains(f)
                        && !given.getfile().get(f).equals("b"
                        + sha1(readContentsAsString(join(f))))) {
                    System.out.println("There is an untracked file in "
                            + "the way; delete it or add it first.");
                    return;
                }
            }

            for (String name : keys) {
                String sha1 = (String) given.getfile().get(name);
                Blob b = readObject(join(".gitlet/objects", sha1),
                        Blob.class);
                writeContents(join(System.getProperty("user.dir"), name),
                        b.content());
            }

            for (String name : keyscurrent) {
                if (!keys.contains(name)) {
                    join(System.getProperty("user.dir"), name).delete();
                }
            }

            writeContents(join(".gitlet/HEAD"), givenbranch.getBytes());
            for (String f : plainFilenamesIn(".gitlet/stage")) {
                join(".gitlet/stage", f).delete();
            }
            writeObject(join(".gitlet/stage/index"), new Stage());
        } else {
            System.out.println("No such branch exists.");
        }
    }

    /**
     * @param operands is input args.
     *                 Perform the command 'branch operands[0].
     */
    void doBranch(String... operands) {
        File branch = join(".gitlet/refs/heads", operands[0]);
        if (operands[0].split("/").length == 2) {
            join(".gitlet/refs/heads", operands[0]
                    .split("/")[0]).mkdir();
        }
        if (!branch.exists()) {
            String head = readContentsAsString(join(".gitlet/HEAD"));
            Commit current = readObject(join(".gitlet/refs/heads", head),
                    Commit.class);
            writeObject(branch, current);
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    /**
     * @param operands is input args
     *                 Perform the command 'rm operands[0]'.
     */
    void doRMbranch(String[] operands) {
        String branch = operands[0];
        String head = readContentsAsString(join(".gitlet/HEAD"));
        String path = ".gitlet/refs/heads";
        if (join(path, branch).exists()) {
            if (!branch.equals(head)) {
                (join(path, branch)).delete();
            } else {
                System.out.println("Cannot remove the current branch.");
            }
        } else {
            System.out.println("A branch with that name does not exist.");
        }
    }

    /**
     * @param operands is input args.
     *                 Perform the command 'reset operands[0]'.
     */
    @SuppressWarnings("unchecked")
    void doReset(String[] operands) {
        String commitid = operands[0];
        String head = readContentsAsString(join(".gitlet/HEAD"));
        Commit current = readObject(join(".gitlet/refs/heads", head),
                Commit.class);
        String path = ".gitlet/objects";
        boolean find = false;
        for (String sha1 : plainFilenamesIn(path)) {
            if (sha1.charAt(0) == 'c') {
                if (commitid.regionMatches(true, 0,
                        sha1, 0, commitid.length())) {
                    find = true;
                    Commit commit = readObject(join(path, sha1), Commit.class);
                    Set<String> keysgiven = commit.getfile().keySet();
                    Set<String> keyscurrent = current.getfile().keySet();
                    for (String f : plainFilenamesIn(System.getProperty
                            ("user.dir"))) {
                        if (!keyscurrent.contains(f) && keysgiven.contains(f)
                                && !commit.getfile().get(f).equals("b"
                                + sha1(readContentsAsString(join(f))))) {
                            System.out.println("There is an untracked file "
                                    + "in the way; delete it or add it first.");
                            return;
                        }
                    }

                    for (String name : keysgiven) {
                        String[] file = new String[3];
                        file[0] = commit.getsha1();
                        file[1] = "--";
                        file[2] = name;
                        doCheckout(file);
                    }

                    for (String f : plainFilenamesIn(System.getProperty
                            ("user.dir"))) {
                        if (!keysgiven.contains(f)) {
                            join(f).delete();
                        }
                    }

                    writeObject(join(".gitlet/refs/heads", head),
                            commit);
                    writeObject(join(".gitlet/stage/index"), new Stage());
                }
            }
        }

        if (!find) {
            System.out.println("No commit with that id exists.");
        }
    }

    /**
     * @param operands is input args.
     *                 Perform the command 'merge operands[0]'.
     */
    @SuppressWarnings("unchecked")
    void doMerge(String... operands) {
        String givenbranch = operands[0];
        String currentbranch = readContentsAsString(join(".gitlet/HEAD"));
        String path = ".gitlet/refs/heads";

        if (!join(path, givenbranch).exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (currentbranch.equals(givenbranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Stage s = readObject(join(".gitlet/stage/index"), Stage.class);
        Delete d = readObject(join(".gitlet/delete"), Delete.class);
        if (!s.getfile().isEmpty() | !d.getfile().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        Commit given = readObject(join(path, givenbranch), Commit.class);
        Commit current = readObject(join(path, currentbranch), Commit.class);
        Commit split = null;

        Set<String> keysgiven = given.getfile().keySet();
        Set<String> keyscurrent = current.getfile().keySet();
        for (String f : plainFilenamesIn(System.getProperty
                ("user.dir"))) {
            if (!keyscurrent.contains(f) && keysgiven.contains(f)
                    && !given.getfile().get(f).equals("b"
                    + sha1(readContentsAsString(join(f))))) {
                System.out.println("There is an untracked file in the "
                        + "way; delete it or add it first.");
                return;
            }
        }
        doMerge(current, given, split,
                currentbranch, givenbranch, keyscurrent, keysgiven);

    }

    /**
     * @param currentbranch is current branch.
     * @param givenbranch   is given branch.
     * @param split         is the commit where two branches splits.
     * @param keyscurrent is the list of file names in current commit
     * @param keysgiven is the list of file names in given commit
     * @param current is current commit
     * @param given is given commit
     */
    void doMerge(Commit current, Commit given, Commit split,
                 String currentbranch, String givenbranch,
                 Set<String> keyscurrent, Set<String> keysgiven) {
        String pathcommit = ".gitlet/objects";
        ArrayList<String> pastcommit1 = new ArrayList<String>();
        ArrayList<String> pastcommit2 = new ArrayList<String>();
        String currentsha1 = current.getsha1();

        while (join(pathcommit, currentsha1).exists()) {
            pastcommit1.add(currentsha1);
            current = readObject(join(pathcommit, currentsha1), Commit.class);
            currentsha1 = current.parent();
        }

        if (pastcommit1.contains(given.getsha1())) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            return;
        }
        String givensha1 = given.getsha1();
        while (join(pathcommit, givensha1).exists()) {
            pastcommit2.add(givensha1);
            if (givensha1.equals(pastcommit1.get(0))) {
                System.out.println("Current branch fast-forwarded.");
                writeObject(join(".gitlet/refs/heads", currentbranch),
                        pastcommit2.get(0));
                return;
            } else if (pastcommit1.contains(givensha1)) {
                split = readObject(join(pathcommit, givensha1),
                        Commit.class);
                break;
            }
            given = readObject(join(pathcommit, givensha1), Commit.class);
            givensha1 = given.parent();
        }

        doMerge(currentbranch, givenbranch, split, keyscurrent, keysgiven);
    }


    /**
     * @param currentbranch is current branch.
     * @param givenbranch   is given branch.
     * @param split         is the commit where two branches splits.
     * @param keyscurrent is the list of file names in current commit
     * @param keysgiven is the list of file names in given commit
     */
    @SuppressWarnings("unchecked")
    void doMerge(String currentbranch, String givenbranch,
                 Commit split, Set keyscurrent, Set keysgiven) {
        Set<String> keys = split.getfile().keySet();
        boolean conflict = false;
        String path = ".gitlet/refs/heads";
        String pathcommit = ".gitlet/objects";
        Commit given = readObject(join(path, givenbranch), Commit.class);
        Commit current = readObject(join(path, currentbranch), Commit.class);
        for (String name : keys) {
            if (!split.getfile().get(name).equals(given.getfile().get(name))
                    && split.getfile().get(name).equals
                    (current.getfile().get(name))) {
                if (given.getfile().get(name) == null) {
                    String[] filetodelete = new String[1];
                    filetodelete[0] = (String) split.getfile().get(name);
                    doRemove(name);
                } else {
                    Blob b = readObject(join(".gitlet/objects",
                            (String) given.getfile().get(name)), Blob.class);
                    writeContents(join(name), b.content());
                    doAdd(name);
                }
            } else if (!split.getfile().get(name)
                    .equals(given.getfile().get(name))
                    && !split.getfile().get(name)
                    .equals(current.getfile().get(name))) {
                conflict = printconflict(given, current, name,
                        pathcommit, conflict);
            }
        }

        for (String name : (Set<String>) keysgiven) {
            if (!keys.contains(name) && !keyscurrent.contains(name)) {
                String[] file = new String[3];
                file[0] = given.getsha1();
                file[1] = "--";
                file[2] = name;
                doCheckout(file);
                doAdd(name);
            }
        }

        if (conflict) {
            System.out.println("Encountered a merge conflict. ");
        }

        doCommit(String.format("Merged %s into %s.", givenbranch,
                currentbranch), current.getsha1(), given.getsha1());
    }


    /**
     * @param current is current branch.
     * @param given   is given branch.
     * @param name         is the commit where two branches splits.
     * @param pathcommit is the list of file names in current commit
     * @param conflict is the list of file names in given commit
     * @return true if has confilict.
     */
    boolean printconflict(Commit given, Commit current, String name,
                       String pathcommit, boolean conflict) {
        if (given.getfile().get(name) != null
                && current.getfile().get(name) != null
                && !given.getfile().get(name)
                .equals(current.getfile().get(name))) {
            Blob givenb = readObject(join(pathcommit,
                    (String) given.getfile().get(name)), Blob.class);
            Blob currentb = readObject(join(pathcommit,
                    (String) current.getfile().get(name)), Blob.class);
            writeContents(join(System.getProperty("user.dir"), name),
                    "<<<<<<< HEAD\n",
                    currentb.content(), "=======\n", givenb.content(),
                    ">>>>>>>\n");
            doAdd(name);
            conflict = true;
        } else if (given.getfile().get(name) == null
                && current.getfile().get(name) != null) {
            Blob currentb = readObject(join(pathcommit,
                    (String) current.getfile().get(name)), Blob.class);
            writeContents(join(System.getProperty("user.dir"), name),
                    "<<<<<<< HEAD\n",
                    currentb.content(), "=======\n", ">>>>>>>\n");
            doAdd(name);
            conflict = true;
        } else if (given.getfile().get(name) != null
                && current.getfile().get(name) == null) {
            Blob givenb = readObject(join(pathcommit,
                    (String) given.getfile().get(name)), Blob.class);
            writeContents(join(System.getProperty("user.dir"), name),
                    "<<<<<<< HEAD\n", "=======\n", givenb.content(),
                    ">>>>>>>\n");
            doAdd(name);
            conflict = true;
        }
        return conflict;
    }


    /**
     * @param operands is input args.
     * do add remote.
     */
    void doAddremote(String... operands) {
        String remotename = operands[0];
        String dir = operands[1];
        if (join(remote(), remotename).exists()) {
            System.out.println("A remote with that name already exists.");
            return;
        }

        new File(remote()).mkdir();
        join(remote(), remotename).mkdir();
        File remotepath = join(remote(), remotename, "path");
        writeContents(remotepath, dir.getBytes());
    }

    /**
     * @param operands is input args.
     * do Fetch from remote.
     */
    void doFetch(String... operands) {
        String remotename = operands[0];
        String dir = readContentsAsString(join(remote(), remotename, "path"));
        String rbranch = operands[1];
        if (!join(dir).exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        if (!join(dir, heads(), rbranch).exists()) {
            System.out.println("That remote does not have that branch.");
            return;
        }
        join(remote(), remotename, rbranch).mkdir();
        String localbranch = remotename + "/" + rbranch;
        if (!join(local(), localbranch).exists()) {
            doBranch(localbranch);
        }
        Commit rcommit = readObject(join(dir, heads(), rbranch), Commit.class);
        List<String> r = plainFilenamesIn(join(dir, objects()));
        List<String> l = plainFilenamesIn(join(".gitlet", objects()));
        for (String f : r) {
            if (!l.contains(f)) {
                try {
                    Files.copy(join(dir, objects(), f).toPath(),
                            join(".gitlet", objects(), f).toPath());
                } catch (IOException excp) {
                    throw new IllegalArgumentException(excp.getMessage());
                }
            }
        }
        writeObject(join(local(), localbranch), rcommit);
    }

    /**
     * @param operands is input args.
     * do Pull from remote.
     */
    void doPull(String... operands) {
        String remotename = operands[0];
        String dir = readContentsAsString(join(remote(), remotename, "path"));
        String rbranch = operands[1];
        String localbranch = remotename + "/" + rbranch;
        doFetch(operands);
        doMerge(localbranch);
    }

    /**
     * @param operands is input args.
     * do Push to remote.
     */
    void doPush(String... operands) {
        String remotename = operands[0];
        String dir = readContentsAsString(join(remote(), remotename, "path"));
        String rbranch = operands[1];
        if (!join(dir).exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        Commit current = readObject(join(local(), localhead()), Commit.class);
        ArrayList<String> pastcommit1 = new ArrayList<String>();
        String currentsha1 = current.getsha1();
        Commit given = null;
        if (join(dir, heads(), rbranch).exists()) {
            given = readObject(join(dir, heads(), rbranch), Commit.class);
        }

        while (join(localobjects(), currentsha1).exists()) {
            pastcommit1.add(currentsha1);
            current = readObject(join(localobjects(), currentsha1),
                    Commit.class);
            currentsha1 = current.parent();
        }

        current = readObject(join(local(), localhead()), Commit.class);
        if (given == null | pastcommit1.contains(given.getsha1())) {
            writeObject(join(local(), localhead()),
                    current);
            List<String> r = plainFilenamesIn(join(dir, objects()));
            List<String> l = plainFilenamesIn(join(".gitlet", objects()));
            for (String f : l) {
                if (!r.contains(f)) {
                    try {
                        Files.copy(join(".gitlet", objects(), f).toPath(),
                                join(dir, objects(), f).toPath());
                    } catch (IOException excp) {
                        throw new IllegalArgumentException(excp.getMessage());
                    }
                }
            }
            writeObject(join(dir, heads(), rbranch), current);
        } else {
            System.out.println("Please pull down remote"
                    + " changes before pushing.");
        }
    }

    /**
     * @param operands is input args.
     * remove the local branch that stores remote ones.
     */
    void doRMremote(String... operands) {
        String remotename = operands[0];
        if (join(remote(), remotename).exists()) {
            File file = join(remote(), remotename);
            deleteDir(file);
        } else {
            System.out.println("A remote with that name does not exist.");
        }
    }

    /**
     * Perform the command that has error.
     */
    void doError(String[] unused) {
        System.out.println("No command with that name exists.");
    }
}
