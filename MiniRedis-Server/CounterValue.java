/**
 * A string that holds a whole number, so INCR can work on it.
 * Real Redis does the same: numeric strings are stored as integers inside.
 *
 * Hierarchy: RedisValue -> StringValue -> CounterValue
 */
public class CounterValue extends StringValue
{
    private long count;

    public CounterValue(long start)
    {
        super(String.valueOf(start));   // runs the StringValue constructor first
        count = start;
    }

    // NOT synchronized on purpose: RaceDemo shows what goes wrong without a lock.
    public long incr()
    {
        count++;
        return count;
    }

    // Overrides StringValue.get(): the number is the real value, not the old text
    public String get()
    {
        return String.valueOf(count);
    }
}
