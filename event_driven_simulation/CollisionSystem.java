package event_driven_simulation;

/******************************************************************************
 *  Compilation:  javac CollisionSystem.java
 *  Execution:    java CollisionSystem n               (n random particles)
 *                java CollisionSystem < input.txt     (from a file)
 *  Dependencies: StdDraw.java Particle.java
 *  Data files:   diffusion.txt
 *                diffusion2.txt
 *                diffusion3.txt
 *
 *  Creates n random particles and simulates their motion according
 *  to the laws of elastic collisions.
 ******************************************************************************/

import java.awt.Color;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 *  The {@code CollisionSystem} class represents a collection of particles
 *  moving in the unit box, according to the laws of elastic collision.
 *  This event-based simulation relies on a priority queue.
 */
public class CollisionSystem {
    private static final double HZ = 0.5; // number of redraw events per clock tick

    private PriorityQueue<Event> pq; // the priority queue
    private double t  = 0.0; // simulation clock time
    private Particle[] particles; // the array of particles

    /**
     * Initializes a system with the specified collection of particles.
     * The individual particles will be mutated during the simulation.
     *
     * @param  particles the array of particles
     */
    public CollisionSystem(Particle[] particles) {
        this.particles = particles.clone(); // defensive copy
    }

    /**
     * updates priority queue with all new events for particle a
     * @param a particle
     * @param limit boundary of time, if the collision time of {@code a} and another
     *              particle exceed {@code limit} then this collision event will not
     *              be added to priority queue.
     */
    private void predict(Particle a, double limit) {
        if (a == null) {
            return;
        }
        // particle-particle collisions
        for (int i = 0; i < particles.length; i++) {
            double dt = a.timeToHit(particles[i]);
            if (t + dt <= limit)
                pq.add(new Event(t + dt, a, particles[i]));
        }

        // particle-wall collisions
        double dtX = a.timeToHitVerticalWall();
        if (t + dtX <= limit) {
            pq.add(new Event(t + dtX, a, null));
        }
        double dtY = a.timeToHitHorizontalWall();
        if (t + dtY <= limit) {
            pq.add(new Event(t + dtY, null, a));
        }
    }

    /**
     * redraw all particles
     * @param limit boundary of time, if the current time is earlier than {@code limit}
     *              then add the update event.
     */
    private void redraw(double limit) {
        StdDraw.clear();
        for (int i = 0; i < particles.length; i++) {
            particles[i].draw();
        }
        StdDraw.show();
        StdDraw.pause(20);
        if (t < limit) {
            pq.add(new Event(t + 1.0 / HZ, null, null));
        }
    }

    /**
     * Simulates the system of particles for the specified amount of time.
     *
     * @param limit the amount of time
     */
    public void simulate(double limit) {
        // initialize PQ with collision events and redraw event
        pq = new PriorityQueue<>();
        for (int i = 0; i < particles.length; i++) {
            predict(particles[i], limit);
        }
        pq.add(new Event(0, null, null)); // redraw event

        // the main event-driven simulation loop
        while (!pq.isEmpty()) {
            // get impending event, discard if invalidated
            Event e = pq.poll();
            if (!e.isValid()) {
                continue;
            }
            Particle a = e.a;
            Particle b = e.b;

            // physical collision, so update positions, and then simulation clock
            for (int i = 0; i < particles.length; i++) {
                particles[i].move(e.time - t);
            }
            t = e.time;

            // process event
            if (a != null && b != null) { // particle-particle collision
                a.bounceOff(b);
            } else if (a != null && b == null) { // particle-vertical wall collision
                a.bounceOffVerticalWall();
            } else if (a == null && b != null) { // particle-horizontal wall collision
                b.bounceOffHorizontalWall();
            } else if (a == null && b == null) {// redraw event
                redraw(limit);
            }

            // update the priority queue with new collisions involving a or b
            predict(a, limit);
            predict(b, limit);
        }
    }

    /***************************************************************************
     *  An event during a particle collision simulation. Each event contains
     *  the time at which it will occur (assuming no supervening actions)
     *  and the particles a and b involved.
     *
     *    -  a and b both null:      redraw event
     *    -  a not null, b null:     collision with vertical wall
     *    -  a null, b not null:     collision with horizontal wall
     *    -  a and b both not null:  binary collision between a and b
     ***************************************************************************/
    private static class Event implements Comparable<Event> {
        private final double time; // time that event is scheduled to occur
        private final Particle a, b; // particles involved in event, possibly null
        private final int countA, countB; // collision counts at event creation

        // create a new event to occur at time t involving a and b
        public Event(double t, Particle a, Particle b) {
            this.time = t;
            this.a = a;
            this.b = b;
            if (a != null) {
                countA = a.count();
            } else {
                countA = -1;
            }
            if (b != null) {
                countB = b.count();
            } else {
                countB = -1;
            }
        }

        // compare times when two events will occur
        public int compareTo(Event that) {
            return Double.compare(this.time, that.time);
        }

        // has any collision occurred between when event was created and now?
        public boolean isValid() {
            if (a != null && a.count() != countA) {
                return false;
            }
            if (b != null && b.count() != countB) {
                return false;
            }
            return true;
        }
    }


    /**
     * Unit tests the {@code CollisionSystem} data type.
     * Reads in the particle collision system from a standard input
     * (or generates {@code N} random particles if a command-line integer
     * is specified); simulates the system.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        StdDraw.setCanvasSize(600, 600);

        // enable double buffering
        StdDraw.enableDoubleBuffering();

        // the array of particles
        Particle[] particles;

        if (argv.length == 1) { // create n random particles
            int n = Integer.parseInt(argv[0]);
            particles = new Particle[n];
            for (int i = 0; i < n; i++)
                particles[i] = new Particle();
        } else { // or read from standard input
            Scanner sc = new Scanner(System.in);
            int n = sc.nextInt();
            particles = new Particle[n];
            for (int i = 0; i < n; i++) {
                double rx = sc.nextDouble();
                double ry = sc.nextDouble();
                double vx = sc.nextDouble();
                double vy = sc.nextDouble();
                double radius = sc.nextDouble();
                double mass = sc.nextDouble();
                int r = sc.nextInt();
                int g = sc.nextInt();
                int b = sc.nextInt();
                Color color = new Color(r, g, b);
                particles[i] = new Particle(rx, ry, vx, vy, radius, mass, color);
            }
        }

        // create collision system and simulate
        CollisionSystem system = new CollisionSystem(particles);
        system.simulate(10000);
    }
}