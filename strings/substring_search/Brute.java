/******************************************************************************
 *  Compilation:  javac Brute.java
 *  Execution:    java Brute pattern text
 *
 *  Reads in two strings, the pattern and the input text, and
 *  searches for the pattern in the input text using brute force.
 *
 *  % java Brute abracadabra abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:               abracadabra
 *
 *  % java Brute rab abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:         rab
 *
 *  % java Brute rabrabracad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                        rabrabracad
 *
 *  % java Brute bcara abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                                   bcara
 *
 *  % java Brute abacad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern: abacad
 *
 ******************************************************************************/

public class Brute {
    /***************************************************************************
     *  A. String versions.
     ***************************************************************************/

    // return offset of first match or n if no match
    public static int search1(String pat, String txt) {
        int m = pat.length();
        int n = txt.length();

        for (int i = 0; i < n; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (txt.charAt(i+j) != pat.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                return i; // found at offset i
            }
        }
        return n; // not found
    }

    // return offset of first match or n if no match
    public static int search2(String pat, String txt) {
        int m = pat.length();
        int n = txt.length();
        int i;
        int j;
        for (i = 0, j = 0; i < n && j < m; i++) {
            if (txt.charAt(i) == pat.charAt(j)) {
                j++;
            } else {
                i = i - j;
                j = 0;
            }
        }
        if (j == m) {
            return i - m; // found
        }
        return n; // not found
    }

    /***************************************************************************
     *  B. char[] array versions.
     ***************************************************************************/

    public static int search1(char[] pattern, char[] text) {
        int m = pattern.length;
        int n = text.length;

        for (int i = 0; i < n; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text[i+j] != pattern[j]) {
                    break;
                }
            }
            if (j == m) {
                return i; // found at offset i
            }
        }
        return n; // not found
    }

    public static int search2(char[] pattern, char[] text) {
        int m = pattern.length;
        int n = text.length;
        int i;
        int j;
        for (i = 0, j = 0; i < n && j < m; i++) {
            if (text[i] == pattern[j]) {
                j++;
            } else {
                i = i - j;
                j = 0;
            }
        }
        if (j == m) {
            return i - m; // found
        }
        return n; // not found
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
        char[] pattern = pat.toCharArray();
        char[] text = txt.toCharArray();

        int offset1a = search1(pat, txt);
        int offset2a = search2(pat, txt);
        int offset1b = search1(pattern, text);
        int offset2b = search2(pattern, text);

        // print results
        System.out.println("text:    " + txt);

        // from brute force search 1a
        System.out.print("pattern: ");
        for (int i = 0; i < offset1a; i++) {
            System.out.print(" ");
        }
        System.out.println(pat);
        // from brute force search 2a
        System.out.print("pattern: ");
        for (int i = 0; i < offset2a; i++) {
            System.out.print(" ");
        }
        System.out.println(pat);
        // from brute force search 1b
        System.out.print("pattern: ");
        for (int i = 0; i < offset1b; i++) {
            System.out.print(" ");
        }
        System.out.println(pat);
        // from brute force search 2b
        System.out.print("pattern: ");
        for (int i = 0; i < offset2b; i++) {
            System.out.print(" ");
        }
        System.out.println(pat);
    }
}
