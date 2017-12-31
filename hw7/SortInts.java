import static java.lang.System.arraycopy;

/** HW #8, Optional Problem 5a.
 *  @author
 */
public class SortInts {

    /** Sort A into ascending order.  Assumes that 0 <= A[i] < n*n for all
     *  i, and that the A[i] are distinct. */
    static void sort(long[] A) {
        sort(A, A.length);
    }

    static public void sort(long[] a, int k) {
        if (k == 0 || k == 1 || a == null) {
            return;
        }

        long max = a[0];
        for (int i = 1; i < k; i++) {
            max = Math.max(max, a[i]);
        }

        int x = k;
        int num = 0;
        while(max>0) {
            num += 1;
            max >>= x;
        }

        for (int i = 0; i < num; i++) {
            sort(a, k, x, i);
        }

    }

    static private void sort(long[] a, int k, int x, int count) {
        int MASK = (1 << (count+1)*x) - 1;
        int[] counts = new int[(int) Math.pow(2,x)];
        long[] output = new long[k];
        for (int i = 0; i < k; i++) {
            long c = (a[i] & MASK) >> (x*count);
            counts[(int) c] += 1;
        }

        for (int i = 0; i < counts.length-1; i++) {
            counts[i+1] += counts[i];
        }

        for (int i = k-1; i >= 0; i--) {
            long c = (a[i] & MASK) >> (x*count);
            output[counts[(int) c]-- -1] = a[i];
        }

        arraycopy(output, 0, a, 0, k);
    }

}

