import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets all the VALUE elements of its input sequence
 *  that are larger than all the preceding values to go through the
 *  Filter.  So, if its input delivers (1, 2, 3, 3, 2, 1, 5), then it
 *  will produce (1, 2, 3, 5).
 *  @author You
 */
class MonotonicFilter<Value extends Comparable<Value>> extends Filter<Value> {

    /** A filter of values from INPUT that delivers a monotonic
     *  subsequence.  */
    MonotonicFilter(Iterator<Value> input) {
        super(input);
    }

    @Override
    protected boolean keep() {
        Value future = candidateNext();
        if (!start) {
            start = true;
            current = future;
            return true;
        } else {
            if (future.compareTo(current) > 0) {
                current = future;
                return true;
            } else {
                return false;
            }
        }
    }

    /* Current stores current value; */
    private Value current;

    /* record if has started the filter or not*/
    private boolean start = false;

}
