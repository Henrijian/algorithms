/******************************************************************************
 *  Compilation:  javac RabinKarp.java
 *  Execution:    java RabinKarp pat txt
 *
 *  Reads in two strings, the pattern and the input text, and
 *  searches for the pattern in the input text using the
 *  Las Vegas version of the Rabin-Karp algorithm.
 *
 *  % java RabinKarp abracadabra abacadabrabracabracadabrabrabracad
 *  pattern: abracadabra
 *  text:    abacadabrabracabracadabrabrabracad
 *  match:                 abracadabra
 *
 *  % java RabinKarp rab abacadabrabracabracadabrabrabracad
 *  pattern: rab
 *  text:    abacadabrabracabracadabrabrabracad
 *  match:           rab
 *
 *  % java RabinKarp bcara abacadabrabracabracadabrabrabracad
 *  pattern: bcara
 *  text:         abacadabrabracabracadabrabrabracad
 *
 *  %  java RabinKarp rabrabracad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                        rabrabracad
 *
 *  % java RabinKarp abacad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern: abacad
 *
 ******************************************************************************/

import java.math.BigInteger;
import java.util.Random;

/**
 *  The RabinKarp class finds the first occurrence of a pattern string
 *  in a text string.
 *  This implementation uses the Rabin-Karp algorithm.
 */
public class RabinKarp {
    private final String pat; // the pattern
    private final long patHash; // pattern hash value
    private final int m; // pattern length
    private final long Q; // a large prime, small enough to avoid long overflow
    private final int R; // the radix
    private long RM; // R^(M-1) % Q

    /**
     * Preprocesses the pattern string.
     *
     * @param pat the pattern string
     */
    public RabinKarp(String pat) {
        this.pat = pat;
        R = 256;
        m = pat.length();
        Q = longRandomPrime();
        RM = 1;
        for (int j = 1; j < m; j++) {
            RM = (RM * R) % Q;
        }
        patHash = hash(pat, m);
    }

    // Compute hash for key[0..m-1].
    private long hash(String key, int m) {
        long h = 0;
        for (int i = 0; i < m; i++) {
            h = (R * h + key.charAt(i)) % Q;
        }
        return h;
    }

    // Las Vegas version: does pat[] match txt[i..i+m-1] ?
    private boolean check(String txt, int i) {
        for (int j = 0; j < m; j++) {
            if (pat.charAt(j) != txt.charAt(i+j)) {
                return false;
            }
        }
        return true;
    }

    // a random 31-bit prime
    private static long longRandomPrime() {
        BigInteger prime = BigInteger.probablePrime(31, new Random());
        return prime.longValue();
    }

    /**
     * Returns the index of the first occurrence of the pattern string
     * in the text string.
     *
     * @param  txt the text string
     * @return the index of the first occurrence of the pattern string
     *         in the text string; n if no such match
     */
    public int search(String txt) {
        int n = txt.length();
        if (n < m) {
            return n;
        }
        long txtHash = hash(txt, m);
        // check for match at offset 0
        if (txtHash == patHash && check(txt, 0)) {
            return 0;
        }
        for (int i = 1; i <= n - m; i++) {
            txtHash = (txtHash + Q -  RM * txt.charAt(i - 1) % Q) % Q;
            txtHash = (txtHash * R + txt.charAt(i + m - 1)) % Q;
            if (txtHash == patHash && check(txt, i)) {
                return i;
            }
        }
        return n;
    }

    /**
     * Takes a pattern string and a text string from command-line arguments;
     * searches for the pattern string in the text string; and prints
     * the first occurrence of the pattern string in the text string.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        String pat = argv[0];
        String txt = argv[1];

        RabinKarp searcher = new RabinKarp(pat);
        int offset = searcher.search(txt);

        // print results
        System.out.println("text:    " + txt);

        // from brute force search method 1
        System.out.print("pattern: ");
        for (int i = 0; i < offset; i++)
            System.out.print(" ");
        System.out.println(pat);
    }
}
