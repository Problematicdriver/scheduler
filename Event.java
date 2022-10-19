import java.util.*;
/*
 * There is 3 types of events each has two properties
 * Time at which it occurs and stop
 */
public abstract class Event implements Comparable<Event> {
    private double time;
    int stopNumber;

    Event(double t, int stopN) {
        time = t;
        stopNumber = stopN;
    }

    public double getTime() {
        return time;
    }
    
    // Comparator for heap
    public int compareTo(Event other) {
        return Double.compare(this.time, other.getTime());
    }


    abstract void run(Simulation sim);
    
    public abstract String toString();
}

class Person extends Event {
    Person(double t, int stopN) {
        super(t, stopN);
    }
    
    // When person arrives queue increments and next person spawn
    public void run(Simulation sim) {
        sim.clock = this.getTime();
        // System.out.println("nextPerson at " + sim.clock+sim.getNextPersonTime());
        Person p = new Person(sim.clock+sim.getNextPersonTime(), stopNumber);
        sim.eventHeap.offer(p);
        sim.stops[stopNumber]++;
        return;
    }

    public String toString() {
        return String.format("Person, %d, %.2f", stopNumber, this.getTime());
    }
}

class Arrival extends Event {
    private int busNumber;
    private int stopCount;

    Arrival(double t, int stopN, int busN, int sCount) {
        super(t, stopN);
        busNumber = busN;
        stopCount = sCount;
    }
    
    // bus arrives and clears the queue at that stop before departing to next stop
    public void run(Simulation sim) {
        sim.clock = this.getTime();
        if (sim.stops[stopNumber] == 0) {
           sim.eventHeap.offer(new Arrival(sim.clock+sim.drive_time, (stopNumber+1)%sim.nStops, busNumber, stopCount+1));
        } else {
            sim.eventHeap.offer(new Boarder(sim.clock, stopNumber, busNumber, stopCount));
        }
        return;
    }

    public String toString() {
        return String.format("Arrival, %d, %.2f, %d, %d", stopNumber, this.getTime(), busNumber, stopCount);
    }
}

class Boarder extends Event {
    private int busNumber;
    private int stopCount;
    Boarder(double t, int stopN, int busN, int sCount) {
        super(t, stopN);
        busNumber = busN;
        stopCount = sCount;
    }
    
    // keep boarding people until queue clears, then bus departs
    public void run(Simulation sim) {
        sim.clock = this.getTime();
        if (sim.stops[stopNumber] == 0) {
            sim.eventHeap.offer(new Arrival(sim.clock+sim.drive_time, (stopNumber+1)%sim.nStops, busNumber, stopCount+1));
        } else {
            sim.stops[stopNumber]--;
            sim.eventHeap.offer(new Boarder(sim.clock+sim.boarding_time, stopNumber, busNumber, stopCount));
        }
        return;
    }

    public String toString() {
        return String.format("Boarder, %d, %.2f, %d", stopNumber, this.getTime(), busNumber);
    }
}
