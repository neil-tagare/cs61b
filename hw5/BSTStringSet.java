import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a BST based String Set.
 *
 * @author Qiuchen Guo
 */
public class BSTStringSet implements StringSet {
    /**
     * Root node of the tree.
     */
    private Node root;

    /**
     * Creates a new empty set.
     */
    public BSTStringSet() {
        root = null;
    }

    @Override
    public void put(String s) {
        if (!contains(s)) {
            root = insert(root, s);
        }
    }

    @Override
    public boolean contains(String s) {
        return contains(root, s);
    }

    @Override
    public List<String> asList() {
        List<String> tlist = asList(root);
        return tlist;
    }

    /**
     * @param T Node
     * @param s Node value
     * @return the node tree
     */
    private Node insert(Node T, String s) {
        if (s == null) {
            return T;
        }
        if (T == null) {
            return new Node(s);
        }
        if (s.compareTo(T.s) < 0) {
            T.left = insert(T.left, s);
        }
        if (s.compareTo(T.s) > 0) {
            T.right = insert(T.right, s);
        }
        return T;
    }

    /**
     * @param T Node
     * @param s value
     * @return true if the node tree contains s
     *         false if does not contain s
     */
    private boolean contains(Node T, String s) {
        boolean contain = false;
        if (T == null || s == null) {
            return contain;
        }
        if (s.compareTo(T.s) == 0) {
            contain = true;
            return contain;
        }
        if (s.compareTo(T.s) < 0) {
            return contains(T.left, s);
        }
        if (s.compareTo(T.s) > 0) {
            return contains(T.right, s);
        }
        return contain;
    }

    /**
     * @param T node tree
     * @return null
     */
    private List<String> asList(Node T) {
        List<String> tlist = new ArrayList<String>();
        if (T == null) {
            return new ArrayList<>();
        }
        if (T.left != null) {
            tlist.addAll(asList(T.left));
        }
        tlist.add(T.s);
        if (T.right != null) {
            tlist.addAll(asList(T.right));
        }
        return tlist;
    }

    /**
     * Represents a single Node of the tree.
     */
    private static class Node {
        /**
         * String stored in this Node.
         */
        private String s;
        /**
         * Left child of this Node.
         */
        private Node left;
        /**
         * Right child of this Node.
         */
        private Node right;

        /**
         * Creates a Node containing SP.
         */
        public Node(String sp) {
            s = sp;
        }
    }
}
