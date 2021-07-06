/******************************************************************************
 *  Compilation:  javac NFA.java
 *  Execution:    java NFA regexp text
 *  Dependencies: Stack.java Bag.java Digraph.java DirectedDFS.java
 *
 *  % java NFA "(A*B|AC)D" AAAABD
 *  true
 *
 *  % java NFA "(A*B|AC)D" AAAAC
 *  false
 *
 *  % java NFA "(a|(bc)*d)*" abcbcd
 *  true
 *
 *  % java NFA "(a|(bc)*d)*" abcbcbcdaaaabcbcdaaaddd
 *  true
 *
 *  Remarks
 *  -----------
 *  The following features are not supported:
 *    - The + operator
 *    - Multiway or
 *    - Metacharacters in the text
 *    - Character classes
 *
 ******************************************************************************/
package strings.regular_expressions;

import graphs.directed_graphs.Digraph;
import graphs.directed_graphs.DirectedDFS;

import java.util.LinkedList;
import java.util.Stack;

/**
 *  The NFA class provides a data type for creating a
 *  nondeterministic finite state automaton(NFA) from a regular
 *  expression and testing whether a given string is matched by that regular
 *  expression.
 *  It supports the following operations: concatenation, closure,
 *  binary or, and parentheses.
 *  It does not support multiway or, character classes,
 *  metacharacters(either in the text or pattern), capturing capabilities,
 *  greedy or reluctant modifiers, and other features in industrial-strength implementations
 *  such as {@link java.util.regex.Pattern} and {@link java.util.regex.Matcher}.
 *
 *  This implementation builds the NFA using a digraph and a stack
 *  and simulates the NFA using digraph search (see the textbook for details).
 *  The constructor takes time proportional to m, where m is the number of characters in the regular expression.
 *  The recognizes method takes time proportional to m * n,
 *  where n is the number of characters in the text.
 */
public class NFA {
    private String regexp; // regular expression.
    private int m; // length of regular expression.
    private Digraph graph; // the directed graph of epsilon transition.

    /**
     * Initialize the NFA for specified regular expression.
     * @param regexp the regular expression.
     */
    public NFA(String regexp) {
        this.regexp = regexp;
        m = regexp.length();
        graph = new Digraph(m + 1);
        Stack<Integer> ops = new Stack<>();
        for (int i = 0; i < regexp.length(); i++) {
            int lp = i; // left parenthesis
            if (regexp.charAt(i) == '(' || regexp.charAt(i) == '|') {
                ops.push(i);
            } else if (regexp.charAt(i) == ')') {
                int or = ops.pop();

                if (regexp.charAt(or) == '|') { // two-way or(|)
                    lp = ops.pop();
                    graph.addEdge(lp, or + 1);
                    graph.addEdge(or, i);
                } else {
                    lp = or;
                }
            }
            if (i < m - 1 && regexp.charAt(i+1) == '*') { // closure
                graph.addEdge(lp, i+1);
                graph.addEdge(i+1, lp);
            }
            if (regexp.charAt(i) == '(' || regexp.charAt(i) == ')' || regexp.charAt(i) == '*') {
                graph.addEdge(i, i+1);
            }
        }
        if (ops.size() != 0) {
            throw new IllegalArgumentException("Invalid regular expression");
        }
    }

    /**
     * Returns true if the text is matched by the regular expression.
     *
     * @param  txt the text
     * @return {@code true} if the text is matched by the regular expression,
     *         {@code false} otherwise
     */
    public boolean recognizes(String txt) {
        DirectedDFS dfs = new DirectedDFS(graph, 0);
        LinkedList<Integer> pc = new LinkedList<>();
        for (int v = 0; v < graph.V(); v++) {
            if (dfs.marked(v)) {
                pc.add(v);
            }
        }
        // Compute possible NFA states for txt[i+1]
        for (int i = 0; i < txt.length(); i++) {
            if (txt.charAt(i) == '(' || txt.charAt(i) == ')' || txt.charAt(i) == '|' || txt.charAt(i) == '*') {
                throw new IllegalArgumentException("text contain metacharacters.");
            }

            LinkedList<Integer> matched = new LinkedList<>();
            for (int v : pc) {
                if (v == m) {
                    continue;
                }
                if (regexp.charAt(v) == txt.charAt(i) || regexp.charAt(v) == '.') {
                    matched.add(v + 1);
                }
            }
            pc = new LinkedList<>();
            // optimization: if no states matched
            if (matched.size() == 0) {
                break;
            }
            dfs = new DirectedDFS(graph, matched);
            for (int v = 0; v < graph.V(); v++) {
                if (dfs.marked(v)) {
                    pc.add(v);
                }
            }
            // optimization: if no states reachable
            if (pc.size() == 0) {
                break;
            }
        }
        // check for accept state reachable
        for (int v : pc) {
            if (v == m) {
                return true;
            }
        }
        return false;
    }

    /**
     * Unit tests the NFA data type.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        String regexp = "(" + argv[0] + ")";
        String txt = argv[1];
        NFA nfa = new NFA(regexp);
        System.out.println(nfa.recognizes(txt));
    }
}
