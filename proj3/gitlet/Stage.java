package gitlet;

import java.io.Serializable;
import java.util.HashMap;
/**
 * @author  Qiuchen Guo
 * */
public class Stage implements Serializable {
    /** constructor.*/
    Stage() {
        _current = new HashMap<String, String>();
    }
    /**
     * @param  name is the file name.
     * @param  sha1 is the file sha1.
     */
    void put(String name, String sha1) {
        _current.put(name, sha1);
    }

    /**
     * @param  name is the file name.
     */
    void remove(String name) {
        _current.remove(name);
    }

    /**
     * @return hashmap.
     */
    HashMap getfile() {
        return _current;
    }

    /**stores a list of blobs.*/
    private HashMap<String, String> _current;

    /**stores UID.*/
    private static final long serialVersionUID = 3L;
}
