/**
 * Background thread that wakes up every second and deletes expired keys,
 * like Redis's "active expiry". (Keys are also checked when used: "lazy expiry".)
 */
public class ExpiryCleaner extends Thread
{
    private Database db;

    public ExpiryCleaner(Database db)
    {
        super("expiry-cleaner");
        this.db = db;
        setDaemon(true);   // daemon: it will not keep the program alive on its own
    }

    public void run()
    {
        try {
            while (true) {
                Thread.sleep(1000);
                int removed = db.removeExpired();
                if (removed > 0) {
                    System.out.println("[cleaner] removed " + removed + " expired key(s)");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[cleaner] stopped");
        }
    }
}
