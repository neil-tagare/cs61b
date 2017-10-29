package db61b;

import java.util.HashMap;
/** A collection of Tables, indexed by name.
 *  @author QIUCHEN GUO*/
class Database {
    /** An empty database. */
    public Database() {
        tableNames = new HashMap<String, Table>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        if (tableNames.containsKey(name)) {
            return tableNames.get(name);
        }
        return null;
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        tableNames.put(name, table);
    }

    /** table Names stores the key and table. they are mapped 1 to 1
     *
     * */
    private HashMap<String, Table> tableNames;
}
