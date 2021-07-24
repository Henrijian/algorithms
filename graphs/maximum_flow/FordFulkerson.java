/******************************************************************************
 *  Compilation:  javac FordFulkerson.java
 *  Execution:    java FordFulkerson V E
 *  Dependencies: FlowNetwork.java FlowEdge.java Queue.java
 *
 *  Ford-Fulkerson algorithm for computing a max flow and
 *  a min cut using shortest augmenting path rule.
 *
 ******************************************************************************/

package graphs.maximum_flow;

import java.util.LinkedList;
import java.util.Queue;

/**
 *  The {@code FordFulkerson} class represents a data type for computing a
 *  maximum st-flow and minimum st-cut in a flow network.
 *
 *  This implementation uses the Ford-Fulkerson algorithm with
 *  the shortest augmenting path heuristic.
 *  The constructor takes O(E V (E + V)) time, where V is the number of
 *  vertices and E is the number of edges.
 *  In practice, the algorithm will run much faster.
 *  The {@code inCut()} and {@code value()} methods take &Theta;(1) time.
 *  It uses &Theta;(V) extra space (not including the network).
 *
 *  This correctly computes the maxflow and mincut if all arithmetic
 *  performed is without floating-point rounding error or arithmetic
 *  overflow. This is guaranteed to be the case if all edge capacities
 *  and initial flow values are integers and the value of the maxflow
 *  does not exceeds 2<sup>52</sup>.
 */
public class FordFulkerson {
    private static final double FLOATING_POINT_EPSILON = 1E-11;

    private final int V; // number of vertices
    private boolean[] marked; // marked[v] = true iff s -> v path in residual graph.
    private FlowEdge[] edgeTo; // edgeTo[v] = last edge on shortest residual s -> v path
    private double value; // current value of max flow

    /**
     * Compute a maximum flow and minimum cut in the network {@code G}
     * from vertex {@code s} to vertex {@code t}.
     *
     * @param  G the flow network
     * @param  s the source vertex
     * @param  t the sink vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     * @throws IllegalArgumentException unless {@code 0 <= t < V}
     * @throws IllegalArgumentException if {@code s == t}
     * @throws IllegalArgumentException if initial flow is infeasible
     */
    public FordFulkerson(FlowNetwork G, int s, int t) {
        this.V = G.V();
        validate(s);
        validate(t);
        if (s == t) {
            throw new IllegalArgumentException("Source equals sink");
        }
        if (!isFeasible(G, s, t)) {
            throw new IllegalArgumentException("Initial flow is infeasible");
        }

        // while there exists an augmenting path, use it
        value = excess(G, t);
        while (hasAugmentingPath(G, s, t)) {
            // compute the bottleneck capacity
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }

            // augment flow
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle);
            }

            value += bottle;
        }

        assert check(G, s, t);
    }

    /**
     * Return the value of maximum flow
     * @return the value of maximum flow
     */
    public double value() {
        return value;
    }

    /**
     * Returns true if the specified vertex is on the {@code s} side of the mincut.
     *
     * @param  v vertex
     * @return {@code true} if vertex {@code v} is on the {@code s} side of the micut;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean inCut(int v) {
        validate(v);
        return marked[v];
    }

    private void validate(int v) {
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException(String.format("vertex %d is not between 0 and %d", v, V - 1));
        }
    }

    /**
     * Is there an augmenting path?
     * If so, upon termination edgeTo[] will contains a parent-link representation of such a path
     * This implementation finds a shortest augmenting path(fewest number of edges),
     * which performance well both in theory and in practice.
     * @param G flow network
     * @param s source vertex
     * @param t sink vertex
     * @return true iff there is path from source {@code s} to sink {@code t}
     */
    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        marked = new boolean[G.V()];
        edgeTo = new FlowEdge[G.V()];

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        marked[s] = true;
        while (!queue.isEmpty() && !marked[t]) {
            int v = queue.poll();
            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 0.0 && !marked[w]) {
                    marked[w] = true;
                    edgeTo[w] = e;
                    queue.add(w);
                }
            }
        }

        return marked[t];
    }

    /**
     * Excess flow at vertex {@code v} in graph {@code G}
     * @param G graph
     * @param v vertex
     * @return Excess flow at vertex {@code v} in graph {@code G}
     */
    private double excess(FlowNetwork G, int v) {
        double excess = 0.0;
        validate(v);
        for (FlowEdge e : G.adj(v)) {
            if (v == e.from()) {
                excess -= e.flow();
            } else {
                excess += e.flow();
            }
        }
        return excess;
    }

    /**
     * Check whether there is a path from {@code s} to {@code t} in graph {@code G}
     * @param G graph
     * @param s start vertex
     * @param t end vertex
     * @return true iff there is a path from {@code s} to {@code t} in graph {@code G}
     */
    private boolean isFeasible(FlowNetwork G, int s, int t) {
        // check that capacity constraints are satisfied
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if (e.flow() < -FLOATING_POINT_EPSILON || e.flow() > e.capacity() + FLOATING_POINT_EPSILON) {
                    System.err.println("Edge does not satisfy capacity constraints: " + e);
                    return false;
                }
            }
        }

        // check that net flow into a vertex equals zero, except at source and sink
        if (Math.abs(value + excess(G, s)) > FLOATING_POINT_EPSILON) {
            System.err.println("Excess at source = " + excess(G, s));
            System.err.println("Max flow         = " + value);
            return false;
        }
        if (Math.abs(value - excess(G, t)) > FLOATING_POINT_EPSILON) {
            System.err.println("Excess at sink   = " + excess(G, t));
            System.err.println("Max flow         = " + value);
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s || v == t) {
                continue;
            } else if (Math.abs(excess(G, v)) > FLOATING_POINT_EPSILON) {
                System.err.println("Net flow out of " + v + " doesn't equal zero");
                return false;
            }
        }
        return true;
    }

    private boolean check(FlowNetwork G, int s, int t) {
        // check that flow is feasible
        if (!isFeasible(G, s, t)) {
            System.err.println("Flow is infeasible");
            return false;
        }

        // check that s is on the source side of min cut and that t is not on source side
        if (!inCut(s)) {
            System.err.println(String.format("source %d is not on source side of min cut", s));
            return false;
        }
        if (inCut(t)) {
            System.err.println(String.format("sink %d is on source side of min cut", t));
            return false;
        }

        // check that value of min cut = value of max flow
        double mincutValue = 0.0;
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if (e.from() == v && inCut(e.from()) && !inCut(e.to())) {
                    mincutValue += e.flow();
                }
            }
        }
        if (Math.abs(value - mincutValue) > FLOATING_POINT_EPSILON) {
            System.err.println(String.format("Max flow value = %f, min cut value = %f", value, mincutValue));
            return false;
        }

        return true;
    }

    /**
     * Unit tests the {@code FordFulkerson} data type.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        // create flow network with V vertices and E edges
        int V = Integer.parseInt(argv[0]);
        int E = Integer.parseInt(argv[1]);
        int s = 0;
        int t = V-1;
        FlowNetwork G = new FlowNetwork(V, E);
        System.out.println(G);

        // compute maximum flow and minimum cut
        FordFulkerson maxflow = new FordFulkerson(G, s, t);
        System.out.println("Max flow from " + s + " to " + t);
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if ((v == e.from()) && e.flow() > 0) {
                    System.out.println("   " + e);
                }
            }
        }

        // print min-cut
        System.out.print("Min cut: ");
        for (int v = 0; v < G.V(); v++) {
            if (maxflow.inCut(v)) {
                System.out.print(v + " ");
            }
        }
        System.out.println();

        System.out.println("Max flow value = " +  maxflow.value());
    }
}
