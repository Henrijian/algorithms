package strings.suffix_arrays;
/******************************************************************************
 *  Compilation:  javac LongestRepeatedSubstring.java
 *  Execution:    java LongestRepeatedSubstring < file.txt
 *  Dependencies: SuffixArray.java
 *
 *  Reads a text string from stdin, replaces all consecutive blocks of
 *  whitespace with a single space, and then computes the longest
 *  repeated substring in that text using a suffix array.
 *
 *  % java LongestRepeatedSubstring < tinyTale.txt
 *  'st of times it was the '
 ******************************************************************************/

import java.util.Scanner;

/**
 *  The {@code LongestRepeatedSubstring} class provides a {@link SuffixArray}
 *  client for computing the longest repeated substring of a string that
 *  appears at least twice. The repeated substrings may overlap (but must
 *  be distinct).
 */
public class LongestRepeatedSubstring {
    /**
     * Returns the longest repeated substring of the specified string.
     *
     * @param  text the string
     * @return the longest repeated substring that appears in {@code text};
     *         the empty string if no such string
     */
    public static String lrs(String text) {
        SuffixArray sa = new SuffixArray(text);
        String lrs = "";
        int n = text.length();
        for (int i = 1; i < n; i++) {
            int length = sa.lcp(i);
            if (length > lrs.length()) {
                lrs = text.substring(sa.index(i), sa.index(i) + length);
            }
        }
        return lrs;
    }

    /**
     * Unit tests the {@code lrs()} method.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine() + " ");
        }
        String s = sb.toString().replaceAll("\\s+", " ");
        System.out.println("'" + lrs(s) + "'");
    }
}
