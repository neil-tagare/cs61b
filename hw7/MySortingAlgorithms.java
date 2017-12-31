
import java.util.Arrays;

import static java.lang.System.arraycopy;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {

            if (k == 0 || k == 1 || array == null) {
                return;
            }
            for (int i = 1; i < k; i++) {
                int j;
                int x = array[i];
                for (j=i-1; j>=0; j -= 1) {
                    if (array[j] <= x) {
                        break;
                    }
                    array[j+1]=array[j];
                }
                array[j+1]=x;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k == 0 || k == 1 || array == null) {
                return;
            }

            for (int i = 0; i < k; i++) {
                int j;
                int min = array[i];
                int midx = i;
                for (j = i+1; j <k; j++) {
                    if (array[j] < min) {
                        min = array[j];
                        midx = j;
                    }
                }
                array[midx] = array[i];
                array[i] = min;
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k == 0 || k == 1 || array == null) {
                return;
            }

            sort(array, 0, k);
        }


        private void sort(int[] array, int low, int high) {
            if (low == high - 1) {
                return;
            }

            int mid = (low + high) / 2;
            sort(array, low, mid);
            sort(array, mid, high);
            merge(array, low, mid, high);
        }

        private void merge(int[] array, int low, int mid, int high) {
            for (int i = mid; i < high; i ++) {
                int temp = array[i];
                int j;
                for (j = i-1; j >= low; j--) {
                        if (array[j] <= temp) {
                            break;
                        }
                        array[j+1]=array[j];
                }
                    array[j+1]=temp;
            }
    }
        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k == 0 || k == 1 || array == null) {
                return;
            }

            int max = array[0];
            for (int i = 1; i < k; i++) {
                max = Math.max(max, array[i]);
            }

            int[] count = new int[max+1];
            for (int i = 0; i < k; i++) {
                count[array[i]] += 1;
            }

            int index = 0;
            int[] output = new int[k];
            for (int item = 0; item < max+1; item++) {
                while(count[item] != 0) {
                    output[index] = item;
                    count[item] -= 1;
                    index += 1;
                }
            }
            arraycopy(output, 0, array, 0 , k);
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k == 0 || k == 1 || array == null) {
                return;
            }

            ArrayHeap ah = new ArrayHeap( k);
            ah.heapify(array);

            int size = k;
            for (int i = 1; i < ah.size(); i++) {
                array[size-1] = ah.removeMax();
                size -= 1;
            }
        }


        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k == 0 || k == 1 || array == null) {
                return;
            }

            sort(array, 0, k);
        }

        public void sort(int[] array, int low, int high) {
            if (low == high) {
                return;
            }

            int pivotvalue = array[low];
            int pivotloc = low;
            for (int i = low + 1; i < high; i++) {
                if (array[i] < pivotvalue) {
                    int j;
                    for (j = i; j > pivotloc; j--) {
                        swap(array, j, j-1);
                    }
                    pivotloc += 1;
                }
            }

            sort(array, low, pivotloc);
            sort(array, pivotloc +1, high);
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            if (k == 0 || k == 1 || a == null) {
                return;
            }

            int max = a[0];
            for (int i = 1; i < k; i++) {
                max = Math.max(max, a[i]);
            }

            int x = 2;
            int num = 0;
            while(max>0) {
                num += 1;
                max >>= x;
            }

            for (int i = 0; i < num; i++) {
                sort(a, k, x, i);
            }

        }

        private void sort(int[] a, int k, int x, int count) {
            int MASK = (1 << (count+1)*x) - 1;
            int[] counts = new int[(int) Math.pow(2,x)];
            int[] output = new int[k];
            for (int i = 0; i < k; i++) {
                int c = (a[i] & MASK) >> (x*count);
                counts[c] += 1;
            }

            for (int i = 0; i < counts.length-1; i++) {
                counts[i+1] += counts[i];
            }

            for (int i = k-1; i >= 0; i--) {
                int c = (a[i] & MASK) >> (x*count);
                output[counts[c]-- -1] = a[i];
            }

            arraycopy(output, 0, a, 0, k);
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            if (k == 0 || k == 1 || a == null) {
                return;
            }
            int max = a[0];
            for (int i = 1; i < k; i++) {
                max = Math.max(max, a[i]);
            }

            int x = 2;
            int num = 0;
            while(max>0) {
                num += 1;
                max >>= x;
            }

            sort(a, 0, k, x, num-1);
        }


        private void sort(int[] a, int low, int high, int x, int count) {
            if (count < 0 || low == high || low == high-1) {
                return;
            }

            int MASK = (1 << (count+1)*x) - 1;
            int[] counts = new int[(int) Math.pow(2,x)];
            int[] output = new int[high-low];
            for (int i = low; i < high; i++) {
                int c = (a[i] & MASK) >> (x*count);
                counts[c] += 1;
            }

            for (int i = 0; i < counts.length-1; i++) {
                counts[i+1] += counts[i];
            }

            for (int i = high - 1; i >= low; i--) {
                int c = (a[i] & MASK) >> (x*count);
                output[counts[c]-- -1] = a[i];
            }
            arraycopy(output, 0, a, low, high-low);

            for (int c = 0; c < counts.length; c++) {
                if (c == counts.length - 1) {
                    sort(a, counts[c]+low, high, x, count -1);
                } else {
                    sort(a, counts[c]+low, counts[c+1]+low, x, count -1);
                }
            }

        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
