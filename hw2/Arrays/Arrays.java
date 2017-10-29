/* NOTE: The file ArrayUtil.java contains some functions that may be useful
 * in testing your answers. */
import java.lang.*;
/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        if (A == null) {
            return B;
        }
        if (B == null) {
            return A;
        }
        int[] result = new int[A.length + B.length];
        System.arraycopy(A, 0, result, 0, A.length);
        System.arraycopy(B, 0, result, A.length , B.length);
        return result;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        int[] result = new int[A.length-len];
        System.arraycopy(A, 0, result, 0, start );
        System.arraycopy(A, start+len, result, start, A.length-(start+len));
        return result;
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        if (A == null) {
            int[][] result = null;
            return result;
        }

        int count = 1;
        for(int i = 0; i < A.length-1; i++){
            if (A[i] > A[i + 1]){
                count += 1;
            }
        }

        int[][] result = new int[count][];

        int j=0, start =0;
        for (int i = 0; i < A.length; i++) {
            if (i < A.length -1 && A[i] >=  A[i + 1]) {
                result[j] = new int[i + 1 - start];
                System.arraycopy(A, start, result[j], 0, i + 1 - start);
                start = i + 1;
                j = j + 1;
            } else if (i == A.length - 1) {
                result[j] = new int[i + 1 - start];
                System.arraycopy(A, start, result[j], 0, i + 1 - start);
            }
        }

        return result;
    }
}
