package graphs.directed_graphs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *  The Digraph class represents a directed graph of vertices
 *  named 0 through V - 1.
 *  It supports the following two primary operations: add an edge to the digraph,
 *  iterate over all of the vertices adjacent from a given vertex.
 *  It also provides methods for
 *  returning the indegree or outdegree of a vertex,
 *  the number of vertices V in the digraph,
 *  the number of edges E in the digraph,
 *  and the reverse digraph.
 *  Parallel edges and self-loops are permitted.
 *
 *  This implementation uses an adjacency-lists representation, which
 *  is a vertex-indexed array of Set objects.
 *  It uses &Theta;(E + V) space,
 *  where E is the number of edges and V is the number of vertices.
 *  The reverse() method takes &Theta;(E + V) time and space;
 *  all other instance methods take &Theta;(1) time. (Though, iterating over
 *  the vertices returned by adj(int) takes time proportional
 *  to the outdegree of the vertex.)
 *  Constructing an empty digraph with V vertices takes
 *  &Theta;(V) time; constructing a digraph with E edges
 *  and V vertices takes &Theta;(E + V) time.
 */
public class Digraph {
    private final String NEWLINE = System.lineSeparator();

    private final int V; // the number if vertices in digraph
    private int E; // the number of edges in digraph
    private final LinkedList<Integer>[] adj; // the vertex-indexed array of adjacency list of digraph
    private int[] indegree; // the vertex-indexed array of indegree of each vertex in digraph

    /**
     * Instantiate a digraph with V vertices with no edges.
     * @param V the number of vertices.
     */
    public Digraph(int V) {
        if (V < 0) {
            throw new IllegalArgumentException("the number of vertices cannot be negative.");
        }
        this.V = V;
        this.E = 0;
        this.adj = new LinkedList[V];
        for (int v = 0; v < V; v++) {
            this.adj[v] = new LinkedList<>();
        }
        this.indegree = new int[V];
    }

    /**
     * The number of vertices in digraph.
     * @return the number of vertices in digraph.
     */
    public int V() {
        return V;
    }

    /**
     * The number of edges in digraph.
     * @return the number of edges in digraph.
     */
    public int E() {
        return E;
    }

    /**
     * Add edge pointing from vertex v to vertex w in digraph.
     * @param v starting vertex of added edge.
     * @param w ending vertex of added edge.
     * @throws IllegalArgumentException unless both 0 <= v < V and 0 <= w < V
     */
    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        E++;
        adj[v].add(w);
        indegree[w]++;
    }

    /**
     * Vertices connected to v by edge pointing from v
     * @param v the vertex
     * @return the vertices adjacent from vertex v in this digraph, as an iterable
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the number of directed edges incident from vertex v.
     * This is known as the outdegree of vertex v.
     *
     * @param  v the vertex
     * @return the outdegree of vertex v
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    public int outdegree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns the number of directed edges incident to vertex v.
     * This is known as the indegree of vertex v.
     *
     * @param  v the vertex
     * @return the indegree of vertex v
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    public int indegree(int v) {
        validateVertex(v);
        return indegree[v];
    }

    /**
     * Returns the reverse of the digraph.
     *
     * @return the reverse of the digraph
     */
    public Digraph reverse() {
        Digraph reverse = new Digraph(V);
        for (int v = 0; v < V; v++) {
            for (int w : adj[v]) {
                reverse.addEdge(w, v);
            }
        }
        return reverse;
    }

    /**
     * Validate vertex index of v.
     * @param v vertex index.
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    private void validateVertex(int v) {
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException(String.format("vertex index v(%d) out of range(0 - %d).", v, V-1));
        }
    }

    /**
     * Returns a string representation of the graph.
     *
     * @return the number of vertices V, followed by the number of edges E,
     *         followed by the V adjacency lists.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vertices, " + E + " edges " + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(String.format("%d: ", v));
            for (int w : adj[v]) {
                s.append(String.format("%d ", w));
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
}
