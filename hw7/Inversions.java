import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** HW #8, Optional Problem 5b.
 *  @author
 */
public class Inversions {

    /** A main program for testing purposes.  Prints the number of inversions
     *  in the sequence ARGS. */
    public static void main(String[] args) {
        System.out.println(inversions(Arrays.asList(args)));
    }

    /** Return the number of inversions of T objects in ARGS. */
    public static <T extends Comparable<? super T>> int inversions(List<T> args)
    {
        return sort(new ArrayList<T>(args), 0, args.size());
    }

    public static <T extends Comparable<? super T>>
    int sort (List<T> array, int low, int high){
            int count;
            if (low == high - 1) {
                return 0;
            }

            int mid = (low + high) / 2;
            count = sort(array, low, mid);
            count += sort(array, mid, high);
            count += merge(array, low, mid, high);
            return count;
    }

    public static <T extends Comparable<? super T>>
    int merge(List<T> array, int low, int mid, int high) {
        int count = 0;
        for (int i = mid; i < high; i++) {
             T temp = array.get(i);
            int j;
            for (j = i - 1; j >= low; j--) {
                if (array.get(j).compareTo(temp) <= 0) {
                    count += 1;
                    break;
                }
                array.set(j+1, array.get(j));
            }
            array.set(j + 1, temp);
        }
        return count;
    }

}
