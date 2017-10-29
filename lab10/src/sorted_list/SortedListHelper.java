package sorted_list;

import java.util.List;

/**
 * SortedList(tm) Utilities
 *
 * COPYRIGHT 2003 FLIK ENTERPRISES CORP.
 * ALL RIGHTS RESERVED
 *
 * PROPOSITION 65 WARNING:
 * WARNING: This Java file MAY CONTAIN chemicals known to the State of California to cause...
 */
public class SortedListHelper {
    /**
     * Inserts `item` into the sorted list `list`, maintaining its sorted...ness.
     */
    public static <T extends Comparable<T>> void insertIntoSortedList(List<T> list, T item) {
        boolean inserted = false;
        T temp;
        list.add(item);
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            if (item.compareTo(temp) < 0) {
                list.set(i,list.get(list.size()-1));
                list.set(list.size()-1,temp);
            }
        }
    }

    /**
     * Checks if the provided list is sorted.
     *
     * @return true, iff the list is sorted from smallest to largest.
     */
    public static <T extends Comparable<T>> boolean isListSorted(List<T> list) {
        T lastItem = null;
        for (T item : list) {
            if (lastItem != null && lastItem.compareTo(item) > 0) {
                return false;
            }
            lastItem = item;
        }
        return true;
    }
}
