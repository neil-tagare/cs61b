package qirkat;

import com.sun.tools.javac.code.Attribute;

import java.util.*;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;
import static qirkat.Piece.*;


/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Qiuchen GUO
 */
class Board extends Observable {

    /** A new, cleared board at the start of the game. */
    Board() {
        _board = new Piece[size][size];
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;
        String str = "wwwwwwwwwwbb-wwbbbbbbbbbb";
        setPieces(str, _whoseMove.opposite());
        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        this._board = new Piece[size][size];
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            this.set(k, b.get(k));
        }
        _pastmove = b._pastmove;
        _whoseMove = b.whoseMove();
        _gameOver = b._gameOver;
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
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
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }

        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'.  */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        return _board[k % size][k / size].color();
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _board[k % size][k / size]= create(v, col(k), row(k));
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {
        boolean move = false;
        if (mov.isJump()) {
            return checkJump(mov, false);
        }

        if (get(mov.col1(),mov.row1()).isPiece() || !get(mov.fromIndex()).equals(whoseMove())) {
            return false;
        }

        if (mov.isLeftMove() || mov.isRightMove() || mov.isForwardMove(whoseMove())) {
            return true;
        }

        if (index(mov.col0(),mov.row0()) % 2 == 0) {
            return mov.isDiagMove(whoseMove());
        }

        return move;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
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

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    private void getMoves(ArrayList<Move> moves, int k) {
        if (k - 1 > 0 && legalMove(move(col(k), row(k), col(k-1),row(k-1)))) {
            moves.add(move(col(k), row(k), col(k-1),row(k-1)));
        }
        if (k + 1 <= MAX_INDEX && legalMove(move(col(k), row(k), col(k+1),row(k+1)))) {
            moves.add(move(col(k), row(k), col(k+1),row(k+1)));
        }
        if (k - size >= 0 && legalMove(move(col(k), row(k), col(k-size),row(k-size)))) {
            moves.add(move(col(k), row(k), col(k-size),row(k-size)));
        }
        if (k + size <=MAX_INDEX && legalMove(move(col(k), row(k), col(k+size),row(k+size)))) {
            moves.add(move(col(k), row(k), col(k+size),row(k+size)));
        }
        if ( k % 2 == 0) {
            if (k - size - 1 >= 0 && legalMove(move(col(k), row(k), col(k-size-1),row(k-size-1)))) {
                moves.add(move(col(k), row(k), col(k-size-1),row(k-size-1)));
            }
            if (k - size + 1 >= 0 && legalMove(move(col(k), row(k), col(k-size+1),row(k-size+1)))) {
                moves.add(move(col(k), row(k), col(k-size+1),row(k-size+1)));
            }
            if (k + size -1 <= MAX_INDEX && legalMove(move(col(k), row(k), col(k+size-1),row(k+size-1)))) {
                moves.add(move(col(k), row(k), col(k+size-1),row(k+size-1)));
            }
            if (k + size +1 <=MAX_INDEX && legalMove(move(col(k), row(k), col(k+size+1),row(k+size+1)))) {
                moves.add(move(col(k), row(k), col(k+size+1),row(k+size+1)));
            }
        }
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    private void getJumps(ArrayList<Move> moves, int k) {
        Move move;
        Board b = new Board(this);
        boolean con = true;

        if (k - 2 >= 0 && checkJump(move(col(k), row(k), col(k-2), row(k-2)), false)) {
            moves.add(move(col(k), row(k), col(k-2), row(k-2)));
        }
        if (k + 2 <= MAX_INDEX && checkJump(move(col(k), row(k), col(k+2), row(k+2)), false)) {
            moves.add(move(col(k), row(k), col(k+2), row(k+2)));
        }
        if (k - 2*size >= 0 && checkJump(move(col(k), row(k), col(k-2*size), row(k-2*size)), false)) {
            moves.add(move(col(k), row(k), col(k-2*size), row(k-2*size)));
        }
        if (k + 2*size <= MAX_INDEX && checkJump(move(col(k), row(k), col(k+2*size), row(k+2*size)), false)) {
            moves.add(move(col(k), row(k), col(k+2*size), row(k+2*size)));
        }

        if ( k % 2 == 0) {
            if (k - 2*size -2 >= 0 && checkJump(move(col(k), row(k), col(k-2*size-2), row(k-2*size-2)), false)) {
                moves.add(move(col(k), row(k), col(k-2*size-2), row(k-2*size-2)));
            }
            if (k - 2*size + 2 >= 0 && checkJump(move(col(k), row(k), col(k-2*size+2), row(k-2*size+2)), false)) {
                moves.add(move(col(k), row(k), col(k-2*size+2), row(k-2*size+2)));
            }
            if (k + 2*size - 2 <= MAX_INDEX && checkJump(move(col(k), row(k), col(k+2*size-2), row(k+2*size-2)), false)) {
                moves.add(move(col(k), row(k), col(k+2*size-2), row(k+2*size-2)));
            }
            if (k + 2*size + 2 <= MAX_INDEX && checkJump(move(col(k), row(k), col(k+2*size+2), row(k+2*size+2)), false)) {
                moves.add(move(col(k), row(k), col(k+2*size+2), row(k+2*size+2)));
            }
        }

        ArrayList<Move> moveb = new ArrayList<Move>();

        for (int i = 0; i < moves.size(); i++) {
            move = moves.get(i);
            b.makeMove(move);
            b._whoseMove = this.whoseMove();
            while (b.jumpPossible(index(move.col1(),move.row1()))) {
                b.getJumps(moveb, index(move.col1(),move.row1()));
            }

//            for (int j = 0; j < moveb.size(); j++) {
//                moves.set(i, move(moves.get(i), moveb.get(j)));
//            }
            b._whoseMove = b.whoseMove().opposite();
            b.undo();
            System.out.println(moves);
        }

    }

    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go.  */
    boolean checkJump(Move mov, boolean allowPartial) {
        if (mov == null) {
            return true;
        }

        if (Math.abs(mov.row1() - mov.row0()) > 2 || Math.abs(mov.col1() - mov.col0()) > 2) {
            return false;
        }

        if (!get(mov.toIndex()).isPiece() && get(mov.fromIndex()).isPiece() && get(mov.jumpedIndex()).isPiece()) {
            if (get(mov.fromIndex()).equals(whoseMove()) && get(mov.fromIndex()).opposite().equals(get(mov.jumpedIndex()))) {
                return true;
            }
        }

        return false;
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        boolean jump = false;
            if (k - 2 >= 0) {
                jump = jump || checkJump(move(col(k), row(k), col(k-2), row(k-2)), false);
            }
            if (k + 2 <= MAX_INDEX) {
                jump = jump || checkJump(move(col(k), row(k), col(k+2), row(k+2)), false);
            }
            if (k - 2*size >= 0) {
                jump = jump || checkJump(move(col(k), row(k), col(k-2*size), row(k-2*size)), false);
            }
            if (k + 2*size <= MAX_INDEX) {
                jump = jump || checkJump(move(col(k), row(k), col(k+2*size), row(k+2*size)), false);
            }

        if ( k % 2 == 0) {
            if (k - 2*size -2 >= 0) {
                jump = jump || checkJump(move(col(k), row(k), col(k-2*size-2), row(k-2*size-2)), false);
            }
            if (k - 2*size + 2 >= 0) {
                jump = jump || checkJump(move(col(k), row(k), col(k-2*size+2), row(k-2*size+2)), false);
            }
            if (k + 2*size - 2 <= MAX_INDEX) {
                jump = jump || checkJump(move(col(k), row(k), col(k+2*size-2), row(k+2*size-2)), false);
            }
            if (k + 2*size + 2 <= MAX_INDEX) {
                jump = jump || checkJump(move(col(k), row(k), col(k+2*size+2), row(k+2*size+2)), false);
            }
        }
        return jump;
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        assert legalMove(mov);
        _pastmove.push(mov);
        while (mov != null) {
            if (mov.isJump()) {
                set(mov.fromIndex(), EMPTY);
                set(mov.jumpedIndex(), EMPTY);
                set(mov.toIndex(), whoseMove());
            } else {
                set(mov.fromIndex(), EMPTY);
                set(mov.toIndex(), whoseMove());
            }
            mov = mov.jumpTail();
        }

        _whoseMove = whoseMove().opposite();
        setChanged();
        notifyObservers();
    }

    /** Undo the last move, if any. */
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

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        String str = "";

        if (legend) {
            for (int row = size - 1; row >= 0; row--) {
                str += " ";
                str += row+1;
                str += " ";
                for (int col = 0; col < size; col++) {
                    str += " " +  _board[col][row].color().shortName();
                }
                str += "\n";
            }
            str += "    " + "a b c d e";
            return str;
        }

        for (int row = size-1; row >= 0; row--) {
            str += " ";
            for (int col = 0; col < size; col++) {
                str +=" " +  _board[col][row].color().shortName();
            }
            if (row != 0) {
                str += "\n";
            }
        }

        return str;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Board) {
            Board b = (Board) o;
            // Note ... is all other class variables of Board
            return (b.toString().equals(toString()) && _whoseMove == b.whoseMove()
                    && _gameOver == b._gameOver);
        } else {
            return false;
        }
    }

    /** Return true iff there is a move for the current player. */
    private boolean isMove() {
        return false;  // FIXME
    }

    /** An arraylist of board*/
    private Piece[][] _board;

    /** Size of the board at each edge*/
    private int size = 5;

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    private Stack<Move> _pastmove = new Stack();
    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
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

        /** Undo the last move. */
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
