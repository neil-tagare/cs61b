import java.util.*;

public class SetDemo {
    /* Main method that add strings to set
        *  and also print Set
        * */
    public static void main(String[] args) {
        Set<String> s;
        s = new HashSet<String>();
        String content[] = {"papa", "bear", "mama", "bear", "baby", "bear"};
        for (String c : content) {
            s.add(c);
        }
        System.out.println(s);
    }
}
