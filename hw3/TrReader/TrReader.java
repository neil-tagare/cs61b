import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @Qiuchen Guo
 */
public class TrReader extends Reader {
    private final Reader _subReader;
    private final String _from, _to;
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(0) to TO.charAt(0), etc., leaving other characters
     *  unchanged.  FROM and TO must have the same length. */
    public TrReader(Reader str, String from, String to) {
        _subReader = str;
        _from = from;
        _to = to;
    }

    /** Close the Reader supplied to my constructor. */
    public void close() throws IOException {
        _subReader.close();
    }

    /** Return the translation of IN according to my _FROM and _TO. */
    private char convertChar(char in) {
        int k = _from.indexOf(in);
        if (k == -1) {
            return in;
        } else {
            return _to.charAt(k);
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int actualRead = _subReader.read(cbuf, off, len);
        for (int i = off; i < off + actualRead; i += 1) {
            cbuf[i] = convertChar(cbuf[i]);
        }
        return actualRead;
    }

}


