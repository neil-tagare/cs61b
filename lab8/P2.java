import java.util.Scanner;

/**
 * @author Qiuchen Guo
 */
public class P2 {
    /**
     *
     */
    public static void main(String... ignored) {
        /**
         *
         */
        Scanner input = new Scanner(System.in);
        int caseno = 0;
        String line, next;

        while (input.hasNext()) {
            line = input.next();
            next = input.next();
            caseno += 1;

            String preorder = line;
            String inorder = next;

            Tree tree = buildtree(preorder, inorder);
            String postorder = traverse(tree);
            System.out.printf("Case %d: %s%n%n", caseno, postorder);
        }
    }

    /**
     * build a tree
     */
    private static Tree buildtree(String pre, String in) {
        if (in.length() == 1) {
            return new Tree(null, null, in.charAt(0));
        } else if (pre.length() == 1) {
            return new Tree(null, null, pre.charAt(0));
        } else if (pre.isEmpty() && in.isEmpty()) {
            return null;
        } else {
            int rootindex = in.indexOf(pre.charAt(0));
            String leftin = in.substring(0, rootindex);
            String leftpre = pre.substring(1, rootindex + 1);
            String rightin = in.substring(rootindex + 1);
            String rightpre = pre.substring(rootindex + 1);

            return new Tree(buildtree(leftpre, leftin),
                    buildtree(rightpre, rightin), pre.charAt(0));
        }
    }


    /*Traverse the tree in postorder*/
    private static String traverse(Tree tree) {
        if (tree == null) {
            return "";
        } else {
            String post = "";
            post += traverse(tree._left);
            post += traverse(tree._right);
            return String.format("%s%s", post, tree._key);
        }
    }

    /**
     * class tree
     */
    /*Tree Class*/
    static class Tree {

        private Tree _left;
        /**
         * tree
         */
        private Tree _right;
        private Character _key;


        /**
         * @param left  left tree
         * @param right right tree
         * @param key   key value
         */
        Tree(Tree left, Tree right, Character key) {
            _left = left;
            _right = right;
            _key = key;
        }
    }
}

