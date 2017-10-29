import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author  Qiuchen Guo
 */
public class P1 {

    /**
     *
     */
    public static void main(String... ignored) throws IOException {
        InputStreamReader file = new InputStreamReader(System.in);
        BufferedReader input = new BufferedReader(file);
        String thisLine, nextLine;
        int length;
        int fnumber = 0;
        int space;
        ArrayList<Integer> number = new ArrayList<Integer>();
        for (thisLine = input.readLine();
             thisLine != null; thisLine = nextLine) {
            length = thisLine.length();
            nextLine = input.readLine();
            if (length == 0 && nextLine != null && nextLine.length() != 0) {
                fnumber += 1;
                space = 0;
                int minIndex = number.indexOf(Collections.min(number));
                for (Integer c : number) {
                    space += Math.abs(c - number.get(minIndex));
                }
                System.out.printf("Image %d: %d%n%n", fnumber, space);
                number = new ArrayList<Integer>();
            } else if (length != 0) {
                number.add(thisLine.length()
                        - thisLine.replaceAll(" ", "").length());
            } else if ((length == 0 && nextLine == null)
                    || (length == 0 && nextLine.length() == 0)) {
                fnumber += 1;
                space = 0;
                int minIndex = number.indexOf(Collections.min(number));
                for (Integer c : number) {
                    space += Math.abs(c - number.get(minIndex));
                }
                System.out.printf("Image %d: %d%n%n", fnumber, space);
                break;
            }
        }
    }
}
