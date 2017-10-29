package qirkat;

public class Piece {
    /** A new tile with VALUE as its value at (ROW, COL).  This
     *  constructor is private, so all tiles are created by the
     *  factory methods create, move, and merge. */
    private Piece(PieceColor color, char col, char row) {
        _color = color;
        _row = row;
        _col = col;
    }

    /** Return my current row. */
    char row() {
        return _row;
    }

    /** Return my current column. */
    char col() {
        return _col;
    }

    /** Return the value supplied to my constructor. */
    PieceColor color() {
        return _color;
    }

    /** Return a new tile at (ROW, COL) with value VALUE. */
    static Piece create(PieceColor value, char col, char row) {
        return new Piece(value, col, row);
    }

    public String toString() {
        return String.format("%s@(%d, %d)", color(), col(), row());
    }

    /** My value. */
    private final PieceColor _color;

    /** My last position on the board. */
    private final char _row, _col;

}
