import com.sun.tools.javac.code.Attribute;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *  @author Qiuchen Guo
 */

public class ArraysTest {
    /** Test catenate function
     *
     */
    @Test
    public void testcatenate() {
        int[] A = {4, 8, 9};
        int[] B = {1, 6, 7};
        int[] C = {4, 8, 9, 1, 6, 7};
        assertArrayEquals(C, Arrays.catenate(A, B));
        assertArrayEquals(B, Arrays.catenate(null, B));
        assertArrayEquals(A, Arrays.catenate(A, null));
    }

    @Test
    public void testremove() {
        int[] A = {1, 5, 7, 9, 10, 2, 8, 7};
        int[] B = Arrays.remove(A, 2, 3);
        int[] C = {1, 5, 2, 8, 7};
        int[] D = {9, 10, 2, 8, 7};
        int[] E = {1, 5, 7, 9, 10, 2, 8};
        assertArrayEquals(C, B);
        assertArrayEquals(D, Arrays.remove(A, 0, 3));
        assertArrayEquals(A, Arrays.remove(A, 5, 0));
        assertArrayEquals(E, Arrays.remove(A, 7, 1));
    }

    @Test
    public void testnaturalRuns() {
        int[] A = {1, 5, 7, 9, 10, 2, 8, 7};
        int[][] B = new int[][]{
                {1, 5, 7, 9, 10},
                {2, 8},
                {7}
        };
        int[][] C = Arrays.naturalRuns(A);
        assertArrayEquals(B, C);

        int[] D = null;
        assertArrayEquals(null, Arrays.naturalRuns(D));

        int[] F = {5};
        int[][] G = new int[][]{
                {5}
        };
        assertArrayEquals(G, Arrays.naturalRuns(F));
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
