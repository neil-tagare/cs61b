package gitlet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Qiuchen Guo
 * */
public class Delete implements Serializable {

    /**
     * A new Delete, using Current Blobs.
     */
    Delete() {
        _current = new ArrayList<String>();
    }


    /**
     * @param  name is the file name.
     */
    void add(String name) {
        _current.add(name);
    }

    /**
     * @param  name is the file name.
     */
    void remove(String name) {
        _current.remove(name);
    }

    /**
     * @return the current files to be deleted.
     */
    ArrayList<String> getfile() {
        return _current;
    }

    /**stores a list of blobs.*/
    private ArrayList<String> _current;

    /**UID.*/
    private static final long serialVersionUID = 4L;
}
