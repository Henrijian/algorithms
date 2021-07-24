/******************************************************************************
 *  Capacitated edge with a flow in a flow network.
 ******************************************************************************/
package graphs.maximum_flow;

/**
 *  The {@code FlowEdge} class represents a capacitated edge with a
 *  flow in a {@link FlowNetwork}. Each edge consists of two integers
 *  (naming the two vertices), a real-valued capacity, and a real-valued
 *  flow. The data type provides methods for accessing the two endpoints
 *  of the directed edge and the weight. It also provides methods for
 *  changing the amount of flow on the edge and determining the residual
 *  capacity of the edge.
 */
public class FlowEdge {
    // to deal with floating-point roundoff errors
    private static final double FLOATING_POINT_EPSILON = 1E-10;

    private final int v; // from
    private final int w; // to
    private final double capacity; // capacity
    private double flow; // flow

    /**
     * Initializes an edge from vertex {@code v} to vertex {@code w} with
     * the given {@code capacity} and zero flow.
     * @param v the tail vertex
     * @param w the head vertex
     * @param capacity the capacity of the edge
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     * or {@code capacity} is a negative number.
     */
    public FlowEdge(int v, int w, double capacity) {
        if (v < 0) {
            throw new IllegalArgumentException("tail vertex of edge must be non-negative integer");
        }
        if (w < 0) {
            throw new IllegalArgumentException("head vertex of edge must be non-negative integer");
        }
        if (capacity < 0.0) {
            throw new IllegalArgumentException("edge capacity must be non-negative");
        }
        this.v = v;
        this.w = w;
        this.capacity = capacity;
        this.flow = 0.0;
    }

    /**
     * Initializes an edge from vertex {@code v} to vertex {@code w} with
     * the given {@code capacity} and {@code flow}.
     * @param v the tail vertex
     * @param w the head vertex
     * @param capacity the capacity of the edge
     * @param flow the flow on the edge
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     *    or @code capacity} is a negative number
     * @throws IllegalArgumentException unless {@code flow} is between
     *    {@code 0.0} and {@code capacity}.
     */
    public FlowEdge(int v, int w, double capacity, double flow) {
        if (v < 0) {
            throw new IllegalArgumentException("tail vertex of edge must be non-negative integer");
        }
        if (w < 0) {
            throw new IllegalArgumentException("head vertex of edge must be non-negative integer");
        }
        if (capacity < 0.0) {
            throw new IllegalArgumentException("edge capacity must be non-negative");
        }
        if (flow < 0.0) {
            throw new IllegalArgumentException("flow must be non-negative");
        }
        if (flow > capacity) {
            throw new IllegalArgumentException("flow exceeds capacity");
        }
        this.v = v;
        this.w = w;
        this.capacity = capacity;
        this.flow = flow;
    }

    /**
     * Initialize a flow edge from another flow edge.
     * @param e the edge to be copied
     */
    public FlowEdge(FlowEdge e) {
        this.v = e.v;
        this.w = e.w;
        this.capacity = e.capacity;
        this.flow = e.flow;
    }

    /**
     * Return the tail vertex of the edge.
     * @return the tail vertex of the edge.
     */
    public int from() {
        return v;
    }

    /**
     * Return the head vertex of the edge.
     * @return the head vertex of the edge.
     */
    public int to() {
        return w;
    }

    /**
     * Return the capacity of the edge.
     * @return the capacity of the edge.
     */
    public double capacity() {
        return capacity;
    }

    /**
     * Return the flow of the edge.
     * @return the flow of the edge.
     */
    public double flow() {
        return flow;
    }

    /**
     * Returns the endpoint of the edge that is different from the given vertex
     * (unless the edge represents a self-loop in which case it returns the same vertex).
     * @param vertex one endpoint of the edge
     * @return the endpoint of the edge that is different from the given vertex
     *   (unless the edge represents a self-loop in which case it returns the same vertex)
     * @throws IllegalArgumentException if {@code vertex} is not one of the endpoints
     *   of the edge
     */
    public int other(int vertex) {
        if (vertex == v) {
            return w;
        } else if (vertex == w) {
            return v;
        } else {
            throw new IllegalArgumentException("invalid endpoint");
        }
    }

    /**
     * Returns the residual capacity of the edge in the direction
     * to the given {@code vertex}.
     * @param vertex one endpoint of the edge
     * @return the residual capacity of the edge in the direction to the given vertex
     *   If {@code vertex} is the head vertex, the residual capacity equals
     *   {@code capacity() - flow()}; if {@code vertex} is the tail vertex, the
     *   residual capacity equals {@code flow()}.
     * @throws IllegalArgumentException if {@code vertex} is not one of the endpoints of the edge
     */
    public double residualCapacityTo(int vertex) {
        if (vertex == v) {
            return flow; // backward edge
        } else if (vertex == w) {
            return capacity - flow; // forward edge
        } else {
            throw new IllegalArgumentException("invalid endpoint");
        }
    }

    /**
     * Increases the flow on the edge in the direction to the given vertex.
     * If {@code vertex} is the tail vertex, this decreases the flow on the edge by {@code delta};
     * if {@code vertex} is the head vertex, this increases the flow on the edge by {@code delta}.
     * @param vertex one endpoint of the edge
     * @param delta amount by which to increase flow
     * @throws IllegalArgumentException if {@code vertex} is not one of the endpoints
     *   of the edge
     * @throws IllegalArgumentException if {@code delta} makes the flow on
     *   on the edge either negative or larger than its capacity
     * @throws IllegalArgumentException if {@code delta} is negative.
     */
    public void addResidualFlowTo(int vertex, double delta) {
        if (delta < 0.0) {
            throw new IllegalArgumentException("Delta must be non-negative");
        }

        if (vertex == v) {
            flow -= delta; // backward edge
        } else if (vertex == w) {
            flow += delta; // forward edge
        } else {
            throw new IllegalArgumentException("invalid endpoint");
        }

        // round flow to 0 or capacity if within floating-point precision
        if (Math.abs(flow) <= FLOATING_POINT_EPSILON) {
            flow = 0;
        }
        if (Math.abs(capacity - flow) <= FLOATING_POINT_EPSILON) {
            flow = capacity;
        }

        if (flow < 0.0) {
            throw new IllegalArgumentException("flow is negative");
        }
        if (flow > capacity) {
            throw new IllegalArgumentException("flow exceeds capacity");
        }
    }

    /**
     * Return a string representation of the edge
     * @return a string representation of the edge
     */
    public String toString() {
        return String.format("%d -> %d %.2f/%.2f", v, w, flow, capacity);
    }

    /**
     * Unit tests the {@code FlowEdge} data type.
     * @param argv command-line arguments
     */
    public static void main(String[] argv) {
        FlowEdge e = new FlowEdge(12, 23, 4.567, 2.345);
        System.out.println(e);
    }
}
