import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * Implementation of a BST based String Set.
 * @author
 */

public class ECHashStringSetTest {

    @Test
    public void testlinkedlist() {
        LinkedList<String>[] A = new LinkedList[5];
    }

    @Test
    public void testPut() {
        int N = 1000000;
        ECHashStringSet ss = new ECHashStringSet();
        HashSet ss2 = new HashSet();
        String s = "cat";
        for (int i = 0; i < N; i++) {
            s = StringUtils.nextString(s);
            ss.put(s);
            ss2.add(s);
        }
        assertEquals("size does not match", ss2.size(), ss.size());

        boolean contain = true;
        s = "cat";
        for (int i=0; i < N; i++) {
            s = StringUtils.nextString(s);
            if (!ss.contains(s)) {
                contain = false;
            }
        }
        assertEquals(true, contain);

        ECHashStringSet s3 = new ECHashStringSet();
        s3.put("Kiwi");
        s3.put("Pear");
        s3.put("Pineapple");
        s3.put("Apple");
        s3.put("Orange");
        assertEquals("size of hashset", 5, 5);
        s3.put("Kiwi");
        assertEquals("no duplicate is added to hashset", 5, 5);
    }


    public static void main(String[] args) {
    }

}
