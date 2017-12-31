package qirkat;
/** Piece store color row and col.
 *  @author QIUCHEN GUO
 */
public class Piece {
    /**
     * @param color current player
     * @param col column
     * @param row row
     */
    private Piece(PieceColor color, char col, char row) {
        _color = color;
        _row = row;
        _col = col;
    }

    /**
     * Return my current row.
     */
    char row() {
        return _row;
    }

    /**
     * Return my current column.
     */
    char col() {
        return _col;
    }

    /**
     * Return the value supplied to my constructor.
     */
    PieceColor color() {
        return _color;
    }

    /**
     * Return a new tile at (ROW, COL) with value VALUE.
     */
    static Piece create(PieceColor value, char col, char row) {
        return new Piece(value, col, row);
    }

    /**
     * @return string of piece
     * to string.
     * */
    public String toString() {
        return String.format("%s@(%d, %d)", color(), col(), row());
    }

    /**
     * My value.
     */
    private final PieceColor _color;

    /**
     * My last position on the board.
     */
    private final char _row, _col;

}
