/**
 * Shows WHY the database needs synchronization.
 * 4 threads each add 1 to a counter 100000 times, so the answer should be 400000.
 *   Test 1: threads call CounterValue.incr() directly (no lock)  -> updates get lost
 *   Test 2: threads send "INCR hits" through CommandEngine (locked) -> always correct
 */
public class RaceDemo
{
    static final int THREADS = 4;
    static final int TIMES = 100000;

    // One worker thread. It uses either the raw counter or the engine.
    static class Worker extends Thread
    {
        private CounterValue counter;
        private CommandEngine engine;

        Worker(CounterValue counter, CommandEngine engine)
        {
            this.counter = counter;
            this.engine = engine;
        }

        public void run()
        {
            for (int i = 0; i < TIMES; i++) {
                if (engine == null) {
                    counter.incr();
                } else {
                    engine.execute("INCR hits");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("Expected total: " + (THREADS * TIMES));

        CounterValue counter = new CounterValue(0);
        runWorkers(counter, null);
        System.out.println("Test 1 - without synchronization: " + counter.get());

        CommandEngine engine = new CommandEngine(new Database());
        runWorkers(null, engine);
        System.out.println("Test 2 - with synchronization:    " + engine.execute("GET hits"));
    }

    private static void runWorkers(CounterValue counter, CommandEngine engine) throws InterruptedException
    {
        Worker[] workers = new Worker[THREADS];
        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Worker(counter, engine);
            workers[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            workers[i].join();   // wait for every thread to finish
        }
    }
}
