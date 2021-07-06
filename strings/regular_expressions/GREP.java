/******************************************************************************
 *  Compilation:  javac GREP.java
 *  Execution:    java GREP regexp < input.txt
 *  Dependencies: NFA.java
 *
 *  This program takes an RE as a command-line argument and prints
 *  the lines from standard input having some substring that
 *  is in the language described by the RE.
 *
 *  % more tinyL.txt
 *  AC
 *  AD
 *  AAA
 *  ABD
 *  ADD
 *  BCD
 *  ABCCBD
 *  BABAAA
 *  BABBAAA
 *
 *  %  java GREP "(A*B|AC)D" < tinyL.txt
 *  ABD
 *  ABCCBD
 *
 ******************************************************************************/
package strings.regular_expressions;

import java.util.Scanner;

/**
 *  The {@code GREP} class provides a client for reading in a sequence of
 *  lines from standard input and printing to standard output those lines
 *  that contain a substring matching a specified regular expression.
 */
public class GREP {
    // do not instantiate
    private GREP() { }

    /**
     * Interprets the command-line argument as a regular expression
     * (supporting closure, binary or, parentheses, and wildcard)
     * reads in lines from standard input; writes to standard output
     * those lines that contain a substring matching the regular
     * expression.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        String regexp = "(.*" + argv[0] + ".*)";
        NFA nfa = new NFA(regexp);
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (nfa.recognizes(line)) {
                System.out.println(line);
            }
        }
    }
}
