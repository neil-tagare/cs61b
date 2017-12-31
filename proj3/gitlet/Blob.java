package gitlet;

import java.io.Serializable;
import java.io.File;

import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;

/**
 * Blob stores the files content.
 * @author Qiuchen Guo
 * */
class Blob implements Serializable {
    /**
     *@param file is input file.
     * Blob constructor.*/
    Blob(File file) {
        _blob = readContents(file);
        _name = "b" + sha1(_blob);
    }

    /**
     * @return file name
     * get file name.*/
    String getname() {
        return _name;
    }

    /**
     *@return file content in bytes
     *get file content./*/
    byte[] content() {
        return _blob;
    }

    /**
     *file content.*/
    private byte[] _blob;

    /**file name.*/
    private String _name;

    /**SUID.*/
    private static final long serialVersionUID = 1L;
}
