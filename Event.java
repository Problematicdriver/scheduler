import java.util.*;

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

    Arrival(double t, int stopN, int busN) {
        super(t, stopN);
        busNumber = busN;
    }

    public void run(Simulation sim) {
        sim.clock = this.getTime();
        if (sim.stops[stopNumber] == 0) {
           sim.eventHeap.offer(new Arrival(sim.clock+sim.drive_time, (stopNumber+1)%sim.nStops, busNumber));
        } else {
            sim.eventHeap.offer(new Boarder(sim.clock, stopNumber, busNumber));
        }
        return;
    }

    public String toString() {
        return String.format("Arrival, %d, %.2f, %d", stopNumber, this.getTime(), busNumber);
    }
}

class Boarder extends Event {
    private int busNumber;

    Boarder(double t, int stopN,int busN) {
        super(t, stopN);
        busNumber = busN;
    }

    public void run(Simulation sim) {
        sim.clock = this.getTime();
        if (sim.stops[stopNumber] == 0) {
            sim.eventHeap.offer(new Boarder(sim.clock+sim.drive_time, (stopNumber+1)%sim.nStops, busNumber));
        } else {
            sim.stops[stopNumber]--;
            sim.eventHeap.offer(new Boarder(sim.clock+sim.boarding_time, stopNumber, busNumber));
        }
        return;
    }

    public String toString() {
        return String.format("Boarder, %d, %.2f, %d", stopNumber, this.getTime(), busNumber);
    }
}
