/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

/** List problem.
 *  @author
 */
class Lists {
    /**
     * Return the list of lists formed by breaking up L into "natural runs":
     * that is, maximal strictly ascending sublists, in the same order as
     * the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     * then result is the four-item list
     * ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     * Destructive: creates no new IntList items, and may modify the
     * original list pointed to by L.
     */
    static IntList2 naturalRuns(IntList L) {
        if (L == null ) {
            return null;
        }

        IntList current = L;
        IntList sublist = L;
        IntList2 result = new IntList2();

        while (current != null) {
            if (current.tail == null) {
                current = current.tail;
                sublist.tail = null;
                break;
            } else if (current.tail.head > current.head) {
                current = current.tail;
                sublist = sublist.tail;
            } else {
                current = current.tail;
                sublist.tail = null;
                break;
            }
        }

        result = new IntList2(L, naturalRuns(current));
        return result;
    }
}
