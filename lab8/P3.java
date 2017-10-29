import java.util.Scanner;
import java.util.regex.MatchResult;

public class P3 {

    /** Solves #3.*/
    public static void main(String... ignored) {

        String regex = "([0-9]+)\\s+";

        Scanner scanner = new Scanner(System.in);

        while (scanner.findWithinHorizon(regex, 0) != null) {
            MatchResult match = scanner.match();
            int n = Integer.parseInt(match.group(1));
            int[] num = findgood(n);
            String snum = "";
            for (int i = 0; i < n; i++) {
                snum += num[i];
            }
            System.out.printf("The smallest good numeral of length %d is %s.%n", n, snum);
        }
    }

    /* returns the smallest good numerals of length n*/
    private static int[] findgood(int n) {
        int length = 0;
        int[] num = new int[n];
        while (length < n) {
            num[length]= 0;
            boolean good = false;
            while (num[length] <= 3 && !good) {
                num[length] += 1;
                good = true;

                int m = 1;
                int mbound = (length+1) / 2;

                int k = 0;
                while (good && m <= mbound) {
                    int firstend = length - m;
                    while (k < m && good) {
                        good = (part(num, length - k, length)
                                .compareTo(part(num, firstend - k, firstend))) != 0;
                        k += 1;
                    }
                    m += 1;
                }

                while (num[length] == 3 && !good) {
                    length -= 1;
                }
            }

            length += 1;
        }
        return num;
    }

    /*return a num of part of int array*/
    private static String part(int[] num, int start, int end) {
        String n = "";
        for (int i = start; i <= end; i++) {
            n += num[i];
        }
        return n;
    }
    /** Returns true iff N contains adjacent repeated substrings.*/
    private static boolean containsAdjacentRepeat(int n) {
        return true;
    }
}
