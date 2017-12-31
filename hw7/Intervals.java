import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/** HW #8, Problem 3.
 *  @author
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        if (intervals == null) {
            return 0;
        }

        int cl=0;
        ArrayList<int[]> node = new ArrayList<>();

        for (int i = 0; i < intervals.size(); i++) {
            node.add(new int[] {intervals.get(i)[0], 1});
            node.add(new int[] {intervals.get(i)[1], -1});
        }

        Collections.sort(node, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] == o2[0]) {
                    return o2[1];
                } else {
                    return o1[0] - o2[0];
                }
            }
        });

        int start = 0;
        int n = 0;
        for (int i = 0; i < node.size(); i++) {
            if (n == 0) {
                start = node.get(i)[0];
            }
            n += node.get(i)[1];
            if(n == 0) {
                cl += node.get(i)[0] - start;
            }
        }

        return cl;
    }

    /** Test intervals. */
    static final int[][] INTERVALS = {
        {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
    };
    /** Covered length of INTERVALS. */
    static final int CORRECT = 23;

    /** Performs a basic functionality test on the coveredLength method. */
    @Test
    public void basicTest() {
        assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}
