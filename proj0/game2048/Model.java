package game2048;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Observable;
import java.util.ArrayList;

/** The state of a game of 2048.
 *  @author Qiuchen Guo
 */
class Model extends Observable {

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to _board[c][r].  Be careful! This is not the usual 2D matrix
     * numbering, where rows are numbered from the top, and the row
     * number is the *first* index. Rather it works like (x, y) coordinates.
     */

    /** Largest piece value. */
    static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    Model(int size) {
        _board = new Tile[size][size];
        _score = _maxScore = 0;
        _gameOver = false;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there. */
    Tile tile(int col, int row) {
        return _board[col][row];
    }

    /** Return the number of squares on one side of the board. */
    int size() {
        return _board.length;
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current score. */
    int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    void clear() {
        _score = 0;
        _gameOver = false;
        for (Tile[] column : _board) {
            Arrays.fill(column, null);
        }
        setChanged();
    }

    /** Add TILE to the board.  There must be no Tile currently at the
     *  same position. */
    void addTile(Tile tile) {
        assert _board[tile.col()][tile.row()] == null;
        _board[tile.col()][tile.row()] = tile;
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board. */
    boolean tilt(Side side) {
        boolean changed;
        changed = false;
        int[] count = new int[4];
        Arrays.fill(count, 0);
        for (int j = 0; j < size(); j++) {
            ArrayList<Integer> nums = new ArrayList<Integer>();
            for (int i = 0; i < size(); i++) {
                int pcol = side.col(j, i, size());
                int prow = side.row(j, i, size());
                if (_board[pcol][prow] != null) {
                    nums.add(_board[pcol][prow].value());
                }
            }
            int k = 0;
            while (k < nums.size() - 1) {
                if (nums.get(k) == nums.get(k + 1)) {
                    count[j] += 1;
                    k = k + 2;
                } else {
                    k = k + 1;
                }
            }
        }
        for (int i = size() - 2; i >= 0; i--) {
            for (int j = size() - 1; j >= 0; j--) {
                int pcol = side.col(j, i, size());
                int prow = side.row(j, i, size());
                int l = i;
                while (_board[pcol][prow] != null && l <= 2) {
                    int upcol = side.col(j, l + 1, size());
                    int uprow = side.row(j, l + 1, size());
                    Tile tile1 = _board[upcol][uprow];
                    Tile tile2 = _board[pcol][prow];
                    if (tile1 != null && tile1.value() != tile2.value()) {
                        break;
                    } else if (tile1 == null) {
                        setVtile(j, l + 1, side, _board[pcol][prow]);
                        pcol = side.col(j, l + 1, size());
                        prow = side.row(j, l + 1, size());
                        l += 1;
                        changed = true;
                    } else if (tile1.value() == tile2.value() && count[j] > 0) {
                        setVtile(j, l + 1, side, _board[pcol][prow]);
                        _score += 2 * tile1.value();
                        count[j] -= 1;
                        l += 1;
                        changed = true;
                    } else {
                        break;
                    }
                }
            }
        }
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Return the current Tile at (COL, ROW), when sitting with the board
     *  oriented so that SIDE is at the top (farthest) from you. */
    private Tile vtile(int col, int row, Side side) {
        return _board[side.col(col, row, size())][side.row(col, row, size())];
    }

    /** Move TILE to (COL, ROW), merging with any tile already there,
     *  where (COL, ROW) is as seen when sitting with the board oriented
     *  so that SIDE is at the top (farthest) from you. */
    private void setVtile(int col, int row, Side side, Tile tile) {
        int pcol = side.col(col, row, size()),
            prow = side.row(col, row, size());
        if (tile.col() == pcol && tile.row() == prow) {
            return;
        }
        Tile tile1 = vtile(col, row, side);
        _board[tile.col()][tile.row()] = null;

        if (tile1 == null) {
            _board[pcol][prow] = tile.move(pcol, prow);
        } else {
            _board[pcol][prow] = tile.merge(pcol, prow, tile1);
        }
    }

    /** Deternmine whether game is over and update _gameOver and _maxScore
     *  accordingly. */
    private void checkGameOver() {
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                Tile current = _board[j][i];
                if (current == null) {
                    return;
                }
            }
        }
        for (int i = 0; i < size() - 1; i++) {
            for (int j = 0; j < size(); j++) {
                Tile current = _board[j][i];
                Tile upi = _board[j][i + 1];
                if (j < size() - 1) {
                    Tile upj = _board[j + 1][i];
                    if (current.value() == upj.value()) {
                        return;
                    } else if (current.value() == upi.value()) {
                        return;
                    }
                } else if (j == size() - 1 && current.value() == upi.value()) {
                    return;
                }
            }
        }
        _gameOver = true;
        _maxScore = _score;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        out.format("] %d (max: %d)", score(), maxScore());
        return out.toString();
    }

    /** Current contents of the board. */
    private Tile[][] _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

}
