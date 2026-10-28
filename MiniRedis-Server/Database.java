import java.util.ArrayList;
import java.util.HashMap;

/**
 * The in-memory store: key -> value, plus key -> expiry time.
 *
 * Many client threads share ONE Database, so every method is synchronized:
 * only one thread can be inside the Database at a time.
 */
public class Database
{
    private HashMap<String, RedisValue> data = new HashMap<String, RedisValue>();
    private HashMap<String, Long> expiresAt = new HashMap<String, Long>();   // time in milliseconds

    public synchronized RedisValue get(String key)
    {
        removeIfExpired(key);   // "lazy" expiry: check when the key is used
        return data.get(key);
    }

    public synchronized void set(String key, RedisValue value)
    {
        data.put(key, value);
        expiresAt.remove(key);   // like Redis, SET clears any old TTL
    }

    public synchronized boolean delete(String key)
    {
        removeIfExpired(key);
        expiresAt.remove(key);
        return data.remove(key) != null;
    }

    public synchronized String[] keys()
    {
        removeExpired();
        return data.keySet().toArray(new String[0]);
    }

    public synchronized boolean expire(String key, long seconds)
    {
        if (get(key) == null) {
            return false;
        }
        expiresAt.put(key, System.currentTimeMillis() + seconds * 1000);
        return true;
    }

    // Same codes as Redis: -2 = no such key, -1 = key never expires
    public synchronized long ttl(String key)
    {
        if (get(key) == null) {
            return -2;
        }
        Long time = expiresAt.get(key);
        if (time == null) {
            return -1;
        }
        return (time - System.currentTimeMillis() + 999) / 1000;   // round up to whole seconds
    }

    // Called every second by the ExpiryCleaner thread. Returns how many keys were removed.
    public synchronized int removeExpired()
    {
        int removed = 0;
        for (String key : new ArrayList<String>(expiresAt.keySet())) {
            if (removeIfExpired(key)) {
                removed++;
            }
        }
        return removed;
    }

    private boolean removeIfExpired(String key)
    {
        Long time = expiresAt.get(key);
        if (time != null && time <= System.currentTimeMillis()) {
            data.remove(key);
            expiresAt.remove(key);
            return true;
        }
        return false;
    }
}
