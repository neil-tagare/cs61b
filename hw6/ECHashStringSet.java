// REPLACE THIS STUB WITH THE CORRECT SOLUTION.
// The current contents of this file are merely to allow things to compile 
// out of the box. It bears scant relation to a proper solution (for one thing,
// a hash table should not be a SortedStringSet.)

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author
 */
class ECHashStringSet implements StringSet{
    private static double MIN_LOAD = 0.2;
    private static double MAX_LOAD = 5;

    @SuppressWarnings("unchecked")
    public ECHashStringSet(int numBuckets) {
        _size = 0;
        _store = (LinkedList<String>[]) new LinkedList[numBuckets];
        for (int i = 0; i < numBuckets; i += 1) {
            _store[i] = new LinkedList<String>();
        }
    }

    public ECHashStringSet() {
        this((int) (1/MIN_LOAD));
    }

    @Override
    public void put(String s) {
        _size += 1;
        if (s != null) {
            if (_size > _store.length * MAX_LOAD) {
                resize();
            }

            _hashcode = hash(s);
            if (!_store[_hashcode].contains(s)) {
                _store[_hashcode].add(s);
            }
        }
    }

    @Override
    public boolean contains(String s) {
        if (s == null) {
            return  false;
        }
        return _store[hash(s)].contains(s);
    }

    @Override
    public List<String> asList() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < _store.length; i++) {
            if (_store[i] != null) {
                for (int j = 0; j < _store[i].size(); j++) {
                    result.add(_store[i].get(j));
                }
            }
        }
        return result;
    }

    public int size() {
        return _size;
    }

    public void resize() {
        int newBucketCount = _size * 5;
        ECHashStringSet echss = new ECHashStringSet(newBucketCount);

        for (int i = 0; i < _store.length; i++) {
            for (String s : _store[i]) {
                echss.put(s);
            }
        }

        _store = echss._store;
    }

    private int hash(String s) {
        return (s.hashCode() & 0x7fffffff) % _store.length;
    }
    /**Size of the key*/
    private int _size;

    private int _hashcode;

    private LinkedList<String>[] _store;


}
