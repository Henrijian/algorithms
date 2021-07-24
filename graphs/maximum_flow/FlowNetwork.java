/******************************************************************************
 *  Compilation:  javac FlowNetwork.java
 *  Execution:    java FlowNetwork V E
 *  Dependencies: FlowEdge.java
 *
 *  A capacitated flow network, implemented using adjacency lists.
 ******************************************************************************/
package graphs.maximum_flow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 *  The {@code FlowNetwork} class represents a capacitated network
 *  with vertices named 0 through <em>V</em> - 1, where each directed
 *  edge is of type {@link FlowEdge} and has a real-valued capacity
 *  and flow.
 *  It supports the following two primary operations: add an edge to the network,
 *  iterate over all of the edges incident to or from a vertex. It also provides
 *  methods for returning the number of vertices <em>V</em> and the number
 *  of edges <em>E</em>. Parallel edges and self-loops are permitted.
 *
 *  This implementation uses an adjacency-lists representation, which
 *  is a vertex-indexed array of {@link java.util.LinkedList} objects.
 *  All operations take constant time (in the worst case) except
 *  iterating over the edges incident to a given vertex, which takes
 *  time proportional to the number of such edges.
 */
public class FlowNetwork {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private LinkedList<FlowEdge>[] adj;

    /**
     * Initializes an empty flow network with {@code V} vertices and 0 edges.
     * @param V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public FlowNetwork(int V) {
        if (V < 0) {
            throw new IllegalArgumentException("number of vertices in a graph must be non-negative");
        }
        this.V = V;
        this.E = 0;
        adj = (LinkedList<FlowEdge>[]) new LinkedList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new LinkedList<>();
        }
    }

    /**
     * Initializes a random flow network with {@code V} vertices and <em>E</em> edges.
     * The capacities are integers between 0 and 99 and the flow values are zero.
     * @param V the number of vertices
     * @param E the number of edges
     * @throws IllegalArgumentException if {@code V < 0}
     * @throws IllegalArgumentException if {@code E < 0}
     */
    public FlowNetwork(int V, int E) {
        this(V);
        if (E < 0) {
            throw new IllegalArgumentException("number of edges must be non-negative");
        }
        Random random = new Random();
        for (int i = 0; i < E; i++) {
            int v = random.nextInt(V);
            int w = random.nextInt(V);
            double capacity = random.nextDouble() * 100;
            addEdge(new FlowEdge(v, w, capacity));
        }
    }

    /**
     * Initializes a flow network from an input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices and edge capacities,
     * with each entry separated by whitespace.
     * @param in the input stream
     * @throws IllegalArgumentException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     */
    public FlowNetwork(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("input stream is null");
        }
        try {
            Scanner sc = new Scanner(in);
            this.V = sc.nextInt();
            this.E = 0;
            this.adj = (LinkedList<FlowEdge>[]) new LinkedList[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new LinkedList<>();
            }
            int E = sc.nextInt();
            if (E < 0) {
                throw new IllegalArgumentException("number of edges must be non-negative");
            }
            for (int i = 0; i < E; i++) {
                int v = sc.nextInt();
                int w = sc.nextInt();
                validateVertex(v);
                validateVertex(w);
                double capacity = sc.nextDouble();
                addEdge(new FlowEdge(v, w, capacity));
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in FlowNetwork constructor", e);
        }
    }

    /**
     * Returns the number of vertices in the edge-weighted graph.
     * @return the number of vertices in the edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted graph.
     * @return the number of edges in the edge-weighted graph
     */
    public int E() {
        return E;
    }

    /**
     * Adds the edge {@code e} to the network.
     * @param e the edge
     * @throws IllegalArgumentException unless endpoints of edge are between
     *         {@code 0} and {@code V-1}
     */
    public void addEdge(FlowEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    /**
     * Check whether the vertex {@code v} is between 0 and V-1
     * @param v vertex
     * @throws IllegalArgumentException unless {@code 0 <= v <= V-1}
     */
    public void validateVertex(int v) {
        if (v < 0 || v > V - 1) {
            throw new IllegalArgumentException(String.format("vertex %d is not between 0 and %d", v, V - 1));
        }
    }

    /**
     * Returns the edges incident on vertex {@code v} (includes both edges pointing to
     * and from {@code v}).
     * @param v the vertex
     * @return the edges incident on vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<FlowEdge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * @return list of all edges - excludes self loops
     */
    public Iterable<FlowEdge> edges() {
        LinkedList<FlowEdge> result = new LinkedList<>();
        for (int v = 0; v < V; v++) {
            for (FlowEdge e : adj[v]) {
                if (e.to() != v) {
                    result.add(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns a string representation of the flow network.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%d %d" + NEWLINE, V, E));
        for (int v = 0; v < V; v++) {
            s.append(String.format("%d:  ", v));
            for (FlowEdge e : adj[v]) {
                if (e.to() != v) {
                    s.append(e + "  ");
                }
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    /**
     * Unit tests the {@code FlowNetwork} data type.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) throws FileNotFoundException {
        FileInputStream in = new FileInputStream(argv[0]);
        FlowNetwork G = new FlowNetwork(in);
        System.out.println(G);
    }
}
