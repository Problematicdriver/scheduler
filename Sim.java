import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Simulation {
    public  int    nStops;
    private int    nBuses;

    public  double drive_time;
    public  double boarding_time;
    public  double stop_time;
    private double lambda;

    private double timeLimit;
    public  static double clock;
    public  final int MAX_ITER = 20000;

    Random rand;
    PriorityQueue<Event> eventHeap; // using heap to keep track of next event in chronological order
    int[] stops;    // length of queue at each stop
    
    Simulation(int nS, int nB, int dt, int bt, double timeLim, double lam) {
        nStops = nS;
        nBuses = nB;
        timeLimit = timeLim;
        drive_time = dt;
        boarding_time = bt;
        clock = 0.0;
        lambda = lam;
        rand = new Random();
        eventHeap = new PriorityQueue<Event>();
        stops = new int[nStops];

   }

    public double getNextPersonTime() {
        return -Math.log(1-rand.nextDouble()) / lambda; 
    }

    public void init() {
        // spwaning buses with even spaces in between
        for (int i = 0; i < nBuses; i++) {
            int nStop = i * nStops / nBuses;
            eventHeap.offer(new Arrival(0.0, nStop, i, nStop));
        } 
       
        // spawning a person per bus stop
        for (int i = 0; i < nStops; i++) {
            eventHeap.offer(new Person(0.0, i));
        }

    }

    void run() {
        int iter = 0;

        int min = 10000, max = -1, avg = 0;

        do {
            Event nextEvnt = eventHeap.poll();
            System.out.println(nextEvnt.toString());
            if (nextEvnt == null) {
                System.out.printf("Sim aborted");
                return;
            }
            nextEvnt.run(this);           
            for (int i = 0; i < stops.length; i++) {
                min = Math.min(min, stops[i]);
                max = Math.max(max, stops[i]);
                avg += stops[i];
            }

        } while (++iter < MAX_ITER && clock <= timeLimit);
        
        avg /= (iter * stops.length);
        System.out.println("Min queue size = " + min);
        System.out.println("Max queue size = " + max);
        System.out.println("Avg queue size = " + avg);
        return;
    }

    public static void main(String[] args) {
        
        if (args.length != 1) {
            System.out.println("Missing source file");
            return;
        }

        int nS;
        int nB;
        int dt;
        int bt;
        double timeLim;
        double lam;

        try {
            File myObj = new File(args[0]);
            Scanner reader = new Scanner(myObj);
            nS = Integer.parseInt(reader.nextLine());
            nB = Integer.parseInt(reader.nextLine());
            dt = Integer.parseInt(reader.nextLine());
            bt = Integer.parseInt(reader.nextLine());
            timeLim = Double.parseDouble(reader.nextLine());
            lam = Double.parseDouble(reader.nextLine()) / 60;
        } catch (FileNotFoundException e) {
            System.out.println("Source file not found");
            return;
        }

        Simulation sim = new Simulation(nS, nB, dt, bt, timeLim, lam);
        sim.init();
        sim.run();
    }
}
