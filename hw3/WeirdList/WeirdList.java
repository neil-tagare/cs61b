/** A WeirdList holds a sequence of integers.
 * @Qiuchen Guo
 */

public class WeirdList {
    private int head;
    private WeirdList tail;

    /** The empty sequence of integers. */
    public static final WeirdList EMPTY = new EmptyWierdList(0, null); // REPLACE THIS LINE WITH THE

    /** A new WeirdList whose head is HEAD and tail is TAIL. */
    public WeirdList(int head, WeirdList tail) {
        this.head = head;
        this.tail = tail;
    }

    /** Returns the number of elements in the sequence that
     *  starts with THIS. */
    public int length() {
        return 1 + tail.length();  // REPLACE THIS LINE WITH THE RIGHT ANSWER.
    }

    /** Apply FUNC.apply to every element of THIS WeirdList in
     *  sequence, and return a WeirdList of the resulting values. */
    public WeirdList map(IntUnaryFunction func) {
        return new WeirdList(func.apply(head), tail.map(func));  // REPLACE THIS LINE WITH THE RIGHT ANSWER.
    }

    /** Print the contents of THIS WeirdList on the standard output
     *  (on one line, each followed by a blank).  Does not print
     *  an end-of-line. */
    public void print() { System.out.print(head + " ");
        tail.print();
    }

    private static class EmptyWierdList extends WeirdList {
        public EmptyWierdList(int head, WeirdList tail) {
            super(head, tail);
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public WeirdList map(IntUnaryFunction func) {
            return this;
        }
    }

}


















/*
 * Hint: The first non-trivial thing you'll probably do to WeirdList
 * is to fix the EMPTY static variable so that it points at something
 * useful. */

