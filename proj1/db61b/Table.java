package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;
        _columns = new ValueList[columns()];
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        try {
            return _titles.length;
        } catch (IndexOutOfBoundsException excp) {
            throw error("Table is null has not been initialized");
        }
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        try {
            return _titles[k];
        } catch (IndexOutOfBoundsException excp) {
            throw error("Table is null has not been initialized");
        }
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < columns(); i += 1) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        try {
            return _columns[0].size();
        } catch (IndexOutOfBoundsException excp) {
            throw error("Table is empty");
        }
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        boolean addrow;

        if (this == null) {
            throw error("Table is null");
        }

        if (_columns[0] == null) {
            for (int k = 0; k < columns(); k += 1) {
                _columns[k] = new ValueList();
                _columns[k].add(values[k]);
            }
            _index.add(0);
            return true;
        }

        for (int i = 0; i < size(); i += 1) {
            addrow = false;
            for (int j = 0; j < columns(); j += 1) {
                if (!_columns[j].get(i).equals(values[j])) {
                    addrow = true;
                }
            }
            if (!addrow) {
                return false;
            }
        }

        for (int k = 0; k < columns(); k += 1) {
            _columns[k].add(values[k]);
        }

        int l = 0;
        int current, next;

        while (true) {
            if (compareRows(_index.get(l), size() - 1) < 0) {
                l += 1;
                if (l == size() - 1) {
                    _index.add(size() - 1);
                    break;
                }
            } else {
                _index.add(size() - 1);
                current = _index.get(l);
                _index.set(l, size() - 1);

                while (l < size() - 1) {
                    next = _index.get(l + 1);
                    _index.set(l + 1, current);
                    current = next;
                    l++;
                }
                break;
            }
        }
        return true;
    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        if (this == null) {
            throw error("Table is null");
        }

        if (columns == null || rows == null) {
            throw error("Input columns or rows is null");
        }

        String[] values = new String[columns.size()];
        int k = 0;
        for (Column c : columns) {
            values[k] = c.getFrom(rows);
            k += 1;
        }
        return this.add(values);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String rows = input.readLine();
            while (rows != null) {
                String[] nextrow = rows.split(",");
                table.add(nextrow);
                rows = input.readLine();
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < columns() - 1; i++) {
                output.printf("%s,", _titles[i]);
            }
            output.printf("%s%n", _titles[columns() - 1]);
            for (int l = 0; l < size(); l += 1) {
                for (int j = 0; j < columns() - 1; j += 1) {
                    output.printf("%s,", _columns[j].get(_index.get(l)));
                }
                output.printf("%s", _columns[columns() - 1].get(_index.get(l)));
                output.printf("%n");
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        if (this == null || _columns[0] == null) {
            return;
        }

        for (int l = 0; l < size(); l += 1) {
            System.out.print(" ");
            for (int j = 0; j < columns(); j += 1) {
                System.out.format("%s ", _columns[j].get(_index.get(l)));
            }
            System.out.println();
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> c = new ArrayList<Column>();

        if (this == null || columnNames == null) {
            throw error("Input table or column names is null");
        }

        for (int k = 0; k < columnNames.size(); k++) {
            c.add(new Column(columnNames.get(k), this));
        }

        if (conditions == null) {
            for (int i = 0; i < size(); i++) {
                result.add(c, i);
            }
            return result;
        }

        for (int i = 0; i < size(); i++) {
            if (Condition.test(conditions, i)) {
                result.add(c, i);
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> c = new ArrayList<Column>();

        if (this == null || table2 == null || columnNames == null) {
            throw error("Input table or column names is null");
        }

        for (int k = 0; k < columnNames.size(); k++) {
            c.add(new Column(columnNames.get(k), this, table2));
        }


        List<Column> comm1 = new ArrayList<Column>();
        List<Column> comm2 = new ArrayList<Column>();

        for (int i = 0; i < this.columns(); i += 1) {
            for (int j = 0; j < table2.columns(); j += 1) {
                if (this._titles[i].equals(table2._titles[j])) {
                    comm1.add(new Column(this._titles[i], this));
                    comm2.add(new Column(table2._titles[j], table2));
                }
            }
        }

        if (conditions == null) {
            for (int i = 0; i < this.size(); i += 1) {
                for (int j = 0; j < table2.size(); j += 1) {
                    if (this.equijoin(comm1, comm2, i, j)) {
                        result.add(c, i, j);
                    }
                }
            }
            return result;
        }

        for (int i = 0; i < this.size(); i += 1) {
            for (int j = 0; j < table2.size(); j += 1) {
                if (this.equijoin(comm1, comm2, i, j)
                        && Condition.test(conditions, i, j)) {
                    result.add(c, i, j);
                }
            }
        }

        return result;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        if (this == null) {
            throw error("Input table is null");
        }

        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        if (common1 == null || common2 == null) {
            throw error("Input columns are null");
        }

        boolean equal = true;
        for (int i = 0; i < common1.size(); i++) {
            if (common1.get(i).getFrom(row1)
                     .compareTo(common2.get(i).getFrom(row2)) != 0) {
                equal = false;
            }
        }
        return equal;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}



