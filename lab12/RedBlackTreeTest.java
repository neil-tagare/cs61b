import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RedBlackTreeTest {
    @Test
    public void testbuildBtree() {
        BTree.TwoThreeFourNode a = new BTree.TwoThreeFourNode("3");
        BTree.TwoThreeFourNode b = new BTree.TwoThreeFourNode("1");
        BTree.TwoThreeFourNode c = new BTree.TwoThreeFourNode("5", "7");
        BTree.TwoThreeFourNode d = new BTree.TwoThreeFourNode("0");
        BTree.TwoThreeFourNode e = new BTree.TwoThreeFourNode("2");
        BTree.TwoThreeFourNode f = new BTree.TwoThreeFourNode("4");
        BTree.TwoThreeFourNode g = new BTree.TwoThreeFourNode("6");
        BTree.TwoThreeFourNode h = new BTree.TwoThreeFourNode("8", "9");

        a.setChildAt(0, b);
        a.setChildAt(1, c);
        b.setChildAt(0, d);
        b.setChildAt(1, e);
        c.setChildAt(0, f);
        c.setChildAt(1, g);
        c.setChildAt(2, h);

        BTree copy = new BTree();
        copy.root = a;

        RedBlackTree rb = new RedBlackTree(copy);
        System.out.println(rb.asList());

    }


    @Test
    public void testinsert() {
        RedBlackTree rb = new RedBlackTree();
        for (int i = 0; i < 11; i++) {
            rb.insert(i);
        }
        System.out.println(rb.asList());
    }

}
