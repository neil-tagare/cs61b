package db61b;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * The suite of all JUnit tests for the qirkat package.
 *
 * @author P. N. Hilfinger
 */

public class UnitTest {
    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        /* textui.runClasses(); */
    }

    /* Test the get method in Table class*/
    @Test
    public void testAdd() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("NAME");
        titles.add("AGE");
        titles.add("HEIGHT");

        Table A = new Table(titles);
        String[] tom = {"TOM", "21", "6 feet"};
        A.add(tom);
        assertEquals("getBack", "21", A.get(0, 1));

        String[] mary = {"MARY", "27", "5 feet"};
        A.add(mary);
        assertEquals("getBack", "27", A.get(1, 1));

        String[] tom2 = {"TOM", "21", "6 feet"};
        boolean checkadd = A.add(tom2);
        assertEquals(false, checkadd);

        String[] amy = {"AMY", "17", "5 feet 5 inch"};
        A.add(amy);

        String[] amy2 = {"AMY", "18", "5 feet"};
        A.add(amy2);
    }

    /*Junit Test for method print*/
    @Test
    public void testPrint() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("NAME");
        titles.add("AGE");
        titles.add("HEIGHT");

        Table A = new Table(titles);
        String[] tom = {"TOM", "21", "6 feet"};
        A.add(tom);

        String[] mary = {"MARY", "27", "5 feet"};
        A.add(mary);

        String[] amy = {"AMY", "17", "5 feet 5 inch"};
        A.add(amy);

        String[] amy2 = {"AMY", "18", "5 feet"};
        A.add(amy2);

        A.print();
    }

    /*Test the other add method */
    @Test
    public void testAdd2() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("NAME");
        titles.add("AGE");
        titles.add("HEIGHT");

        Table A = new Table(titles);
        String[] tom = {"TOM", "21", "6 feet"};
        A.add(tom);

        String[] mary = {"MARY", "27", "5 feet"};
        A.add(mary);

        String[] amy = {"AMY", "17", "5 feet 5 inch"};
        A.add(amy);

        String[] amy2 = {"AMY", "18", "5 feet"};
        A.add(amy2);

        Integer[] rows = {1};
        ArrayList<Column> c = new ArrayList<Column>();
        c.add(new Column("AGE", A));
        c.add(new Column("HEIGHT", A));
        ArrayList<String> titlesB = new ArrayList<String>();
        titlesB.add("AGE");
        titlesB.add("HEIGHT");
        Table B = new Table(titlesB);
        B.add(c, rows);
        assertEquals("27", B.get(0, 0));
        assertEquals("5 feet", B.get(0, 1));
    }

    /*Test select method with conditions and one table*/
    @Test
    public void testSelect() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("NAME");
        titles.add("AGE");
        titles.add("HEIGHT");

        Table A = new Table(titles);
        String[] tom = {"TOM", "21", "6 feet"};
        A.add(tom);

        String[] mary = {"MARY", "27", "5 feet"};
        A.add(mary);

        String[] amy = {"AMY", "17", "5 feet 5 inch"};
        A.add(amy);

        String[] amy2 = {"AMY", "18", "5 feet"};
        A.add(amy2);

        Integer[] rows = {1};
        ArrayList<Column> c = new ArrayList<Column>();
        c.add(new Column("AGE", A));
        c.add(new Column("HEIGHT", A));
        ArrayList<String> titlesB = new ArrayList<String>();
        titlesB.add("AGE");
        titlesB.add("HEIGHT");

        List<Condition> tcon = new ArrayList<Condition>();
        Condition con = new Condition(new Column("NAME", A), "=", "AMY");
        tcon.add(con);
        Table C = A.select(titlesB, tcon);
        assertEquals("AGE", C.getTitle(0));
        assertEquals("17", C.get(0, 0));
        assertEquals("18", C.get(1, 0));
    }

    /*Test second select method in Table.java*/
    @Test
    public void testSelect2() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("NAME");
        titles.add("AGE");
        titles.add("HEIGHT");

        Table A = new Table(titles);
        String[] tom = {"TOM", "21", "6 feet"};
        A.add(tom);

        String[] mary = {"MARY", "27", "5 feet"};
        A.add(mary);

        String[] amy = {"AMY", "17", "5 feet 5 inch"};
        A.add(amy);

        String[] amy2 = {"AMY", "18", "5 feet"};
        A.add(amy2);

        ArrayList<String> titles2 = new ArrayList<String>();
        titles2.add("SID");
        titles2.add("SEX");
        titles2.add("NAME");
        titles2.add("GRADE");

        Table B = new Table(titles2);
        String[] s1 = {"101", "F", "GRACE", "12"};
        B.add(s1);

        String[] s2 = {"102", "M", "MAX", "11"};
        B.add(s2);

        String[] s3 = {"103", "F", "NICOLE", "13"};
        B.add(s3);

        String[] s4 = {"104", "M", "TOM", "10"};
        B.add(s4);

        String[] s5 = {"105", "F", "AMY", "11"};
        B.add(s5);

        List<Condition> tcon = new ArrayList<Condition>();
        Condition con = new Condition(new Column("GRADE", A, B), "=", "10");
        tcon.add(con);

        ArrayList<String> titlesC = new ArrayList<String>();
        titlesC.add("AGE");
        titlesC.add("SID");
        Table C = A.select(B, titlesC, tcon);
        C.print();
        assertEquals("21", C.get(0, 0));
    }

}
