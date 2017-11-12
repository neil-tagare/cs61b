import java.util.*;

public class BST {
    BSTNode root;

    public BST(LinkedList list) {
        root = linkedListToTree(list.iterator(), list.size());
    }

    /**
     * Provide a descriptive comment for this method here
     */
    private BSTNode linkedListToTree (Iterator iter, int n) {
        BSTNode node = new BSTNode();
        if (iter.hasNext()) {
            if (n == 0) {
                return null;
            }
            if (n == 1) {
                node.item = iter.next();
                return node;
            }
            node.left = linkedListToTree(iter, n / 2);
            node.item = iter.next();
            node.right = linkedListToTree(iter, n - n / 2 -1);
        }
        return node;
    }

    /**
     * Prints the tree to the console.
     */
    public void print() {
        print(root, 0);
    }

    private void print(BSTNode node, int d) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < d; i++) {
            System.out.print("  ");
        }
        System.out.println(node.item);
        print(node.left, d + 1);
        print(node.right, d + 1);
    }

    /**
     * Node for BST.
     */
    static class BSTNode {

        /** Item. */
        Object item;

        /** Left child. */
        BSTNode left;

        /** Right child. */
        BSTNode right;
    }
}
