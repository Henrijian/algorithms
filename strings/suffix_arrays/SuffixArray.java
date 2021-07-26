package strings.suffix_arrays;
/******************************************************************************
 *  Compilation:  javac SuffixArray.java
 *  Execution:    java SuffixArray < input.txt
 *
 *  A data type that computes the suffix array of a string.
 *
 *   % java SuffixArray < abra.txt
 *    i ind lcp rnk  select
 *   ---------------------------
 *    0  11   -   0  "!"
 *    1  10   0   1  "A!"
 *    2   7   1   2  "ABRA!"
 *    3   0   4   3  "ABRACADABRA!"
 *    4   3   1   4  "ACADABRA!"
 *    5   5   1   5  "ADABRA!"
 *    6   8   0   6  "BRA!"
 *    7   1   3   7  "BRACADABRA!"
 *    8   4   0   8  "CADABRA!"
 *    9   6   0   9  "DABRA!"
 *   10   9   0  10  "RA!"
 *   11   2   2  11  "RACADABRA!"
 ******************************************************************************/

import java.util.Arrays;
import java.util.Scanner;

/**
 *  The {@code SuffixArray} class represents a suffix array of a string of
 *  length n.
 *  It supports the selecting the i th smallest suffix,
 *  getting the index of the ith smallest suffix,
 *  computing the length of the longest common prefix between the
 *  i th smallest suffix and the i-1 th smallest suffix,
 *  and determining the rank of a query string (which is the number
 *  of suffixes strictly less than the query string).
 *
 *  This implementation uses a nested class {@code Suffix} to represent
 *  a suffix of a string (using constant time and space) and
 *  {@code Arrays.sort()} to sort the array of suffixes.
 *  The index and length operations takes constant time
 *  in the worst case. The lcp operation takes time proportional to the
 *  length of the longest common prefix.
 *  The select operation takes time proportional
 *  to the length of the suffix and should be used primarily for debugging.
 */

public class SuffixArray {
    private final int n;
    private final String[] suffixes;

    public SuffixArray(String s) {
        n = s.length();
        suffixes = new String[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = s.substring(i);
        }
        Arrays.sort(suffixes);
    }

    /**
     * Size of string
     * @return size of string
     */
    public int length() {
        return n;
    }

    /**
     * index of the i th suffix in string
     * @param i the order in suffix array
     * @return index of the i th suffix in string
     */
    public int index(int i) {
        return n - suffixes[i].length();
    }

    /**
     * i th sorted suffix
     * @param i the specified order in suffixes
     * @return i th sorted suffex
     */
    public String select(int i) {
        return suffixes[i];
    }

    /**
     * number of suffixes strictly less than query
     * @param query query string
     * @return number of suffixes strictly less than query
     */
    public int rank(String query) {
        int lo = 0;
        int hi = n - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int cmp = query.compareTo(suffixes[mid]);
            if (cmp < 0) {
                hi = mid - 1;
            } else if (cmp > 0) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return lo;
    }

    /**
     * length of the longest common prefix of s and t
     * @param s a string
     * @param t a string
     * @return length of the longest common prefix of s and t
     */
    public static int lcp(String s, String t) {
        int n = Math.min(s.length(), t.length());
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) != t.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /**
     * length of the longest common prefix of suffixes(i) and suffixes(i-1)
     * @param i the specified order in suffixes
     * @return length of the longest common prefix of suffixes(i) and suffixes(i-1)
     */
    public int lcp(int i) {
        return lcp(suffixes[i], suffixes[i-1]);
    }

    /**
     * length of the longest common prefix of suffixes(i) and suffixes(j)
     * @param i the specified order in suffixes
     * @return length of the longest common prefix of suffixes(i) and suffixes(j)
     */
    public int lcp(int i, int j) {
        return lcp(suffixes[i], suffixes[j]);
    }

    public static void main(String[] argv) {
        Scanner sc = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine() + " ");
        }
        String s = sb.toString();
        SuffixArray suffix = new SuffixArray(s);
        System.out.println("  i ind lcp rnk  select");
        System.out.println("---------------------------");
        for (int i = 0; i < s.length(); i++) {
            int index = suffix.index(i);
            String ith = "\"" + s.substring(index, Math.min(index + 50, s.length())) + "\"";
            int rank = suffix.rank(suffix.select(i));
            if (i == 0) {
                System.out.printf("%3d %3d %3s %3d  %s\n", i, index, "-", rank, ith);
            } else {
                int lcp = suffix.lcp(i, i-1);
                System.out.printf("%3d %3d %3d %3d  %s\n", i, index, lcp, rank, ith);
            }
        }
    }
}
