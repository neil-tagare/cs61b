package qirkat;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import static qirkat.Move.*;
import static qirkat.Piece.create;
import static qirkat.PieceColor.*;


/**
 * A Qirkat board.   The squares are labeled by column (a char value between
 * 'a' and 'e') and row (a char value between '1' and '5'.
 * <p>
 * For some purposes, it is useful to refer to squares using a single
 * integer, which we call its "linearized index".  This is simply the
 * number of the square in row-major order (with row 0 being the bottom row)
 * counting from 0).
 * <p>
 * Moves on this board are denoted by Moves.
 *
 * @author Qiuchen GUO
 */
class Board extends Observable {

    /**
     * A new, cleared board at the start of the game.
     */
    Board() {
        _board = new Piece[size][size];
        for (int k = 0; k <= MAX_INDEX; k++) {
            previous[k] = 0;
        }
        clear();
    }

    /**
     * A copy of B.
     */
    Board(Board b) {
        internalCopy(b);
    }

    /**
     * Return a constant view of me (allows any access method, but no
     * method that modifies it).
     */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /**
     * Clear me to my starting state, with pieces in their initial
     * positions.
     */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;
        String str = "wwwwwwwwwwbb-wwbbbbbbbbbb";
        setPieces(str, _whoseMove);
        for (int k = 0; k <= MAX_INDEX; k++) {
            previous[k] = 0;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Copy B into me.
     */
    void copy(Board b) {
        internalCopy(b);
    }

    /**
     * Copy B into me.
     */
    @SuppressWarnings("unchecked")
    private void internalCopy(Board b) {
        this._board = new Piece[size][size];
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            this.set(k, b.get(k));
            this.previous[k] = b.previous[k];
        }

        this._pastmove = (Stack<Move>) b._pastmove.clone();
        _whoseMove = b.whoseMove();
        _gameOver = b._gameOver;
    }

    /**
     * Set my contents as defined by STR.  STR consists of 25 characters,
     * each of which is b, w, or -, optionally interspersed with whitespace.
     * These give the contents of the Board in row-major order, starting
     * with the bottom row (row 1) and left column (column a). All squares
     * are initialized to allow horizontal movement in either direction.
     * NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b':
            case 'B':
                set(k, BLACK);
                break;
            case 'w':
            case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }

        this._whoseMove = nextMove;
        setChanged();
        notifyObservers();
    }

    /**
     * Return true iff the game is over: i.e., if the current player has
     * no moves.
     */
    boolean gameOver() {
        if (!isMove()) {
            _gameOver = true;
        }
        return _gameOver;
    }

    /**
     * Return the current contents of square C R, where 'a' <= C <= 'e',
     * and '1' <= R <= '5'.
     */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /**
     * Return the current contents of the square at linearized index K.
     */
    PieceColor get(int k) {
        assert validSquare(k);
        return _board[k % size][k / size].color();
    }

    /**
     * Set get(C, R) to V, where 'a' <= C <= 'e', and
     * '1' <= R <= '5'.
     */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /**
     * Set get(K) to V, where K is the linearized index of a square.
     */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _board[k % size][k / size] = create(v, col(k), row(k));
    }

    /**
     * Return true iff MOV is legal on the current board.
     */
    boolean legalMove(Move mov) {
        if (mov.isJump()) {
            return checkJump(mov, false);
        }

        if (get(mov.col1(), mov.row1()).isPiece()
                || !get(mov.fromIndex()).equals(whoseMove())) {
            notifyObservers();
            return false;
        }

        if (jumpPossible() && !mov.isJump()) {
            return false;
        }

        if (mov.isForwardMove(whoseMove())) {
            return true;
        }

        if ((mov.isLeftMove() && previous[mov.fromIndex()] != 1)
                || (mov.isRightMove() && previous[mov.fromIndex()] != -1)) {
            if (whoseMove() == WHITE && mov.fromIndex()
                    >= SIDE * 4 && mov.fromIndex()
                    <= SIDE * 5 - 1) {
                return false;
            } else if (whoseMove() == BLACK && mov.fromIndex()
                    >= 0 && mov.fromIndex() <= 4) {
                return false;
            }
            return true;
        }

        if (index(mov.col0(), mov.row0()) % 2 == 0) {
            return mov.isDiagMove(whoseMove());
        }

        return false;
    }

    /**
     * Return a list of all legal moves from the current position.
     */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<Move>();
        getMoves(result);
        return result;
    }

    /**
     * Add all legal moves from the current position to MOVES.
     */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }

        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getJumps(moves, k);
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /**
     * Add all legal non-capturing moves from the position
     * with linearized index K to MOVES.
     */
    private void getMoves(ArrayList<Move> moves, int k) {
        if (k - 1 > 0
                && legalMove(move(col(k), row(k), col(k - 1), row(k - 1)))) {
            moves.add(move(col(k), row(k), col(k - 1), row(k - 1)));
        }
        if (k + 1 <= MAX_INDEX
                && legalMove(move(col(k), row(k), col(k + 1), row(k + 1)))) {
            moves.add(move(col(k), row(k), col(k + 1), row(k + 1)));
        }
        if (k - size >= 0 && legalMove(move(col(k), row(k),
                col(k - size), row(k - size)))) {
            moves.add(move(col(k), row(k), col(k - size), row(k - size)));
        }
        if (k + size <= MAX_INDEX && legalMove(move(col(k),
                row(k), col(k + size), row(k + size)))) {
            moves.add(move(col(k), row(k), col(k + size), row(k + size)));
        }
        if (k % 2 == 0) {
            if (k - size - 1 >= 0 && legalMove(move(col(k), row(k),
                    col(k - size - 1), row(k - size - 1)))) {
                moves.add(move(col(k), row(k),
                        col(k - size - 1), row(k - size - 1)));
            }
            if (k - size + 1 >= 0 && legalMove(move(col(k), row(k),
                    col(k - size + 1), row(k - size + 1)))) {
                moves.add(move(col(k), row(k),
                        col(k - size + 1), row(k - size + 1)));
            }
            if (k + size - 1 <= MAX_INDEX && legalMove(move(col(k), row(k),
                    col(k + size - 1), row(k + size - 1)))) {
                moves.add(move(col(k), row(k),
                        col(k + size - 1), row(k + size - 1)));
            }
            if (k + size + 1 <= MAX_INDEX && legalMove(move(col(k), row(k),
                    col(k + size + 1), row(k + size + 1)))) {
                moves.add(move(col(k), row(k),
                        col(k + size + 1), row(k + size + 1)));
            }
        }
    }

    /**
     * Add all legal captures from the position with linearized index K
     * to MOVES.
     */
    @SuppressWarnings("unchecked")
    private void getJumps(ArrayList<Move> moves, int k) {
        if (k - 2 >= 0 && checkJump(move(col(k), row(k),
                col(k - 2), row(k - 2)), false)) {
            moves.addAll(mjump(k - 2, move(col(k), row(k),
                    col(k - 2), row(k - 2))));
        }
        if (k + 2 <= MAX_INDEX && checkJump(move(col(k), row(k),
                col(k + 2), row(k + 2)), false)) {
            moves.addAll(mjump(k + 2, move(col(k), row(k),
                    col(k + 2), row(k + 2))));
        }
        if (k - 2 * size >= 0 && checkJump(move(col(k), row(k),
                col(k - 2 * size), row(k - 2 * size)), false)) {
            moves.addAll(mjump(k - 2 * size, move(col(k), row(k),
                    col(k - 2 * size), row(k - 2 * size))));
        }
        if (k + 2 * size <= MAX_INDEX && checkJump(move(col(k), row(k),
                col(k + 2 * size), row(k + 2 * size)), false)) {
            moves.addAll(mjump(k + 2 * size, move(col(k), row(k),
                    col(k + 2 * size), row(k + 2 * size))));
        }

        if (k % 2 == 0) {
            if (k - 2 * size - 2 >= 0 && checkJump(move(col(k), row(k),
                    col(k - 2 * size - 2), row(k - 2 * size - 2)), false)) {
                moves.addAll(mjump(k - 2 * size - 2, move(col(k), row(k),
                        col(k - 2 * size - 2), row(k - 2 * size - 2))));
            }
            if (k - 2 * size + 2 >= 0 && checkJump(move(col(k), row(k),
                    col(k - 2 * size + 2), row(k - 2 * size + 2)), false)) {
                moves.addAll(mjump(k - 2 * size + 2, move(col(k), row(k),
                        col(k - 2 * size + 2), row(k - 2 * size + 2))));
            }
            if (k + 2 * size - 2 <= MAX_INDEX && checkJump(move(col(k), row(k),
                    col(k + 2 * size - 2), row(k + 2 * size - 2)), false)) {
                moves.addAll(mjump(k + 2 * size - 2, move(col(k), row(k),
                        col(k + 2 * size - 2), row(k + 2 * size - 2))));
            }
            if (k + 2 * size + 2 <= MAX_INDEX && checkJump(move(col(k), row(k),
                    col(k + 2 * size + 2), row(k + 2 * size + 2)), false)) {
                moves.addAll(mjump(k + 2 * size + 2, move(col(k), row(k),
                        col(k + 2 * size + 2), row(k + 2 * size + 2))));
            }
        }

    }

    /**
     * @param k    index on board.
     * @param move the move to this location
     * @return the possible moves from this location linked with follow moves
     * recursively find move
     */
    private ArrayList mjump(int k, Move move) {
        Board b = new Board(this);
        ArrayList<Move> movea = new ArrayList<Move>();
        ArrayList<Move> moveb = new ArrayList<Move>();
        b.makeMove(move);
        b._whoseMove = this.whoseMove();
        if (!b.jumpPossible(k)) {
            moveb.add(move);
            return moveb;
        } else {
            b.getJumps(moveb, k);
            for (int i = 0; i < moveb.size(); i++) {
                movea.add(move(move, moveb.get(i)));
            }
            return movea;
        }
    }


    /**
     * Return true iff MOV is a valid jump sequence on the current board.
     * MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     * could be continued and are valid as far as they go.
     */
    boolean checkJump(Move mov, boolean allowPartial) {
        if (mov == null) {
            return true;
        }

        if (Math.abs(mov.row1() - mov.row0()) > 2
                || Math.abs(mov.col1() - mov.col0()) > 2) {
            return false;
        }

        if (!allowPartial) {
            if (!get(mov.toIndex()).isPiece() && get(mov.fromIndex()).isPiece()
                    && get(mov.jumpedIndex()).isPiece()) {
                if (get(mov.fromIndex()).equals(whoseMove())
                        && get(mov.fromIndex()).opposite()
                        .equals(get(mov.jumpedIndex()))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * check a series move is legal or not.
     * @param mov move to check.
     * @return true if mov is legal
     * */
    boolean checkJump(Move mov) {
        Board b = new Board(this);
        boolean more = false;
        while (mov != null) {
            if (b.checkJump(mov, false)) {
                b.set(mov.fromIndex(), EMPTY);
                b.set(mov.jumpedIndex(), EMPTY);
                b.set(mov.toIndex(), whoseMove());
                more = b.jumpPossible(mov.col1(), mov.row1());
                mov = mov.jumpTail();
            } else {
                return false;
            }
        }

        if (more) {
            return false;
        }

        return true;
    }

    /**
     * Return true iff a jump is possible for a piece at position C R.
     */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /**
     * Return true iff a jump is possible for a piece at position with
     * linearized index K.
     */
    boolean jumpPossible(int k) {
        boolean jump = false;
        if (k - 2 >= 0) {
            jump = jump || checkJump(move(col(k), row(k), col(k - 2),
                    row(k - 2)), false);
        }
        if (k + 2 <= MAX_INDEX) {
            jump = jump || checkJump(move(col(k), row(k), col(k + 2),
                    row(k + 2)), false);
        }
        if (k - 2 * size >= 0) {
            jump = jump || checkJump(move(col(k), row(k), col(k - 2 * size),
                    row(k - 2 * size)), false);
        }
        if (k + 2 * size <= MAX_INDEX) {
            jump = jump || checkJump(move(col(k), row(k), col(k + 2 * size),
                    row(k + 2 * size)), false);
        }

        if (k % 2 == 0) {
            if (k - 2 * size - 2 >= 0) {
                jump = jump || checkJump(move(col(k), row(k),
                        col(k - 2 * size - 2), row(k - 2 * size - 2)), false);
            }
            if (k - 2 * size + 2 >= 0) {
                jump = jump || checkJump(move(col(k), row(k),
                        col(k - 2 * size + 2), row(k - 2 * size + 2)), false);
            }
            if (k + 2 * size - 2 <= MAX_INDEX) {
                jump = jump || checkJump(move(col(k), row(k),
                        col(k + 2 * size - 2), row(k + 2 * size - 2)), false);
            }
            if (k + 2 * size + 2 <= MAX_INDEX) {
                jump = jump || checkJump(move(col(k), row(k),
                        col(k + 2 * size + 2), row(k + 2 * size + 2)), false);
            }
        }
        return jump;
    }

    /**
     * Return true iff a jump is possible from the current board.
     */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the color of the player who has the next move.  The
     * value is arbitrary if gameOver().
     */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /**
     * Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     * other than pass, assumes that legalMove(C0, R0, C1, R1).
     */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /**
     * Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     * Assumes the result is legal.
     */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /**
     * Make the Move MOV on this Board, assuming it is legal.
     */
    void makeMove(Move mov) {
        assert legalMove(mov);
        _pastmove.push(mov);
        while (mov != null) {
            if (mov.isJump()) {
                set(mov.fromIndex(), EMPTY);
                set(mov.jumpedIndex(), EMPTY);
                set(mov.toIndex(), whoseMove());
                for (int k = 0; k <= MAX_INDEX; k++) {
                    if (whoseMove() == get(k) || k == mov.jumpedIndex()) {
                        previous[k] = 0;
                    }
                }
            } else {
                set(mov.fromIndex(), EMPTY);
                set(mov.toIndex(), whoseMove());
                if (mov.isLeftMove()) {
                    previous[mov.toIndex()] = -1;
                } else if (mov.isRightMove()) {
                    previous[mov.toIndex()] = 1;
                } else {
                    previous[mov.toIndex()] = 0;
                }
            }
            mov = mov.jumpTail();
        }

        _whoseMove = whoseMove().opposite();
        setChanged();
        notifyObservers();
    }

    /**
     * Undo the last move, if any.
     */
    void undo() {
        Move mov = _pastmove.pop();
        while (mov != null) {
            if (mov.isJump()) {
                set(mov.toIndex(), EMPTY);
                set(mov.jumpedIndex(), whoseMove());
                set(mov.fromIndex(), whoseMove().opposite());
            } else {

                set(mov.toIndex(), EMPTY);
                set(mov.fromIndex(), whoseMove().opposite());
            }
            mov = mov.jumpTail();
        }

        _whoseMove = whoseMove().opposite();
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * Return a text depiction of the board.  If LEGEND, supply row and
     * column numbers around the edges.
     */
    String toString(boolean legend) {
        String str = "";

        if (legend) {
            for (int row = size - 1; row >= 0; row--) {
                str += " ";
                str += row + 1;
                str += " ";
                for (int col = 0; col < size; col++) {
                    str += " " + _board[col][row].color().shortName();
                }
                str += "\n";
            }
            str += "    " + "a b c d e";
            return str;
        }

        for (int row = size - 1; row >= 0; row--) {
            str += " ";
            for (int col = 0; col < size; col++) {
                str += " " + _board[col][row].color().shortName();
            }
            if (row != 0) {
                str += "\n";
            }
        }

        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Board) {
            Board b = (Board) o;
            return (b.toString().equals(toString())
                    && _whoseMove == b.whoseMove()
                    && _gameOver == b._gameOver);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _board.hashCode();
    }

    /**
     * Return true iff there is a move for the current player.
     */
    private boolean isMove() {
        if (jumpPossible()) {
            return true;
        }

        for (int k = 0; k <= MAX_INDEX; k++) {
            ArrayList<Move> moves = new ArrayList<>();
            getMoves(moves, k);
            if (!moves.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * .
     * An arraylist of board
     */
    private Piece[][] _board;

    /**
     * .
     * Size of the board at each edge
     */
    private int size = 5;

    /**
     * .
     * Player that is on move.
     */
    private PieceColor _whoseMove;

    /**
     * Set true when game ends.
     */
    private boolean _gameOver;

    /**
     * .
     * store pastmoves
     */
    private Stack<Move> _pastmove = new Stack<Move>();

    /**
     * An array that stores one previous location,
     * if previous[c][r]= -1, it means it
     * comes from left, if 1 means it comes from right.
     * if 0, it can make anymove it
     * wants
     */
    private Integer[] previous = new Integer[size * size];

    /**
     * Convenience value giving values of pieces at each ordinal position.
     */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /**
     * One cannot create arrays of ArrayList<Move>, so we introduce
     * a specialized private list type for this purpose.
     */
    private static class MoveList extends ArrayList<Move> {
    }

    /**
     * A read-only view of a Board.
     */
    private class ConstantBoard extends Board implements Observer {
        /**
         * A constant view of this Board.
         */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /**
         * Undo the last move.
         */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}
