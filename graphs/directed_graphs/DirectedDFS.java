/******************************************************************************
 *  Compilation:  javac DirectedDFS.java
 *  Execution:    java DirectedDFS digraph.txt s
 *
 *  Determine single-source or multiple-source reachability in a digraph
 *  using depth first search.
 *  Runs in O(E + V) time.
 *
 *  % java DirectedDFS tinyDG.txt 1
 *  1
 *
 *  % java DirectedDFS tinyDG.txt 2
 *  0 1 2 3 4 5
 *
 *  % java DirectedDFS tinyDG.txt 1 2 6
 *  0 1 2 3 4 5 6 8 9 10 11 12
 *
 ******************************************************************************/
package graphs.directed_graphs;

import javax.sound.sampled.Line;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *  The DirectedDFS class represents a data type for
 *  determining the vertices reachable from a given source vertex s
 *  (or set of source vertices) in a digraph.
 *
 *  This implementation uses depth-first search.
 *  The constructor takes time proportional to V + E (in the worst case),
 *  where V is the number of vertices and E is the number of edges.
 *  Each instance method takes &Theta;(1) time.
 *  It uses &Theta;(V) extra space (not including the digraph).
 */
public class DirectedDFS {
    private boolean marked[]; // marked[v] = true iff the vertex v is reachable from s.
    private int count; // the number of vertices reachable from s.

    /**
     * Computes the vertices in digraph G that are
     * reachable from the source vertex s.
     * @param G the digraph
     * @param s the source vertex
     * @throws IllegalArgumentException unless  0 <= s < V
     */
    public DirectedDFS(Digraph G, int s) {
        marked = new boolean[G.V()];
        validateVertex(s);
        dfs(G, s);
    }

    /**
     * Computes the vertices in digraph G that are
     * connected to any of the source vertices sources.
     * @param G the graph
     * @param sources the source vertices
     * @throws IllegalArgumentException if sources is null
     * @throws IllegalArgumentException unless 0 <= s < V
     *         for each vertex s in sources
     */
    public DirectedDFS(Digraph G, Iterable<Integer> sources) {
        marked = new boolean[G.V()];
        validateVertices(sources);
        for (int v : sources) {
            if (!marked[v]) dfs(G, v);
        }
    }

    /**
     * Check whether the vertex v is in digraph.
     * @param v the vertex
     * @throws IllegalArgumentException if the vertex is not in digraph.
     */
    private void validateVertex(int v) {
        if (v < 0 || v >= marked.length) {
            throw new IllegalArgumentException(String.format("the vertex v(%s) is not in range [0 - %d)", v, marked.length));
        }
    }

    /**
     * Check whether all the vertex in vertices in digraph.
     * @param vertices the list of vertex
     * @throws IllegalArgumentException if vertices is null
     * @throws IllegalArgumentException if the vertex is not in digraph.
     */
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("vertices is null.");
        }
        int count = 0;
        for (int v : vertices) {
            count++;
            validateVertex(v);
        }
        if (count == 0) {
            throw new IllegalArgumentException("number of vertices is zero");
        }
    }

    private void dfs(Digraph G, int v) {
        count++;
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }

    /**
     * Is there a directed path from the source vertex (or any
     * of the source vertices) and vertex v?
     * @param  v the vertex
     * @return true if there is a directed path, false otherwise
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    /**
     * Returns the number of vertices reachable from the source vertex
     * (or source vertices).
     * @return the number of vertices reachable from the source vertex
     *   (or source vertices)
     */
    public int count() {
        return count;
    }

    /**
     * Unit tests the DirectedDFS data type.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) throws FileNotFoundException {

        // read in digraph from command-line argument
        FileInputStream in = new FileInputStream(argv[0]);
        Digraph G = new Digraph(in);

        // read in sources from command-line arguments
        LinkedList<Integer> sources = new LinkedList<>();
        for (int i = 1; i < argv.length; i++) {
            int s = Integer.parseInt(argv[i]);
            sources.add(s);
        }

        // multiple-source reachability
        DirectedDFS dfs = new DirectedDFS(G, sources);

        // print out vertices reachable from sources
        for (int v = 0; v < G.V(); v++) {
            if (dfs.marked(v)) {
                System.out.print(v + " ");
            }
        }
        System.out.println();
    }
}
