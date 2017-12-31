import java.util.Arrays;

/** HW #8, Optional Problem 5c.
 * @author
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        A = Arrays.copyOf(A, A.length);
        Arrays.sort(A);
        for (int i = 0; i < B.length; i++) {
            int residue = m - B[i];
            int j = Arrays.binarySearch(A, residue);
            if (j >= 0 && j < A.length && A[j]+B[i]==m) {
                return true;
            }
        }
        return false;
    }

}
