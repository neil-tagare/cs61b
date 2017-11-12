import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BSTTest {

    @Test
    public void testcon() {
        LinkedList a = new LinkedList();
        for (int i = 0; i < 10; i++) {
            a.add(i);
        }

        BST bst = new BST(a);
        bst.print();
    }

}
