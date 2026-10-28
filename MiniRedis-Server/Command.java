import java.util.ArrayList;

/**
 * Base class for every command (SET, GET, INCR...).
 * Each subclass only writes execute(); run() checks the argument count first.
 */
public abstract class Command
{
    private String name;
    private int minArgs;
    private int maxArgs;   // -1 means "no limit"

    public Command(String name, int minArgs, int maxArgs)
    {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    public String getName()
    {
        return name;
    }

    // final: every command gets the same argument check
    public final String run(Database db, String[] args) throws RedisException
    {
        if (args.length < minArgs || (maxArgs != -1 && args.length > maxArgs)) {
            throw new InvalidArgumentException(
                "wrong number of arguments for '" + name.toLowerCase() + "' command");
        }
        return execute(db, args);
    }

    protected abstract String execute(Database db, String[] args) throws RedisException;

    // ---- helpers that format replies the way redis-cli prints them ----

    protected static String text(String s)
    {
        return s == null ? "(nil)" : "\"" + s + "\"";
    }

    protected static String integer(long n)
    {
        return "(integer) " + n;
    }

    protected static String list(ArrayList<String> items)
    {
        if (items.isEmpty()) {
            return "(empty array)";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append(i + 1).append(") ").append(text(items.get(i)));
        }
        return sb.toString();
    }

    protected static long parseNumber(String s) throws InvalidArgumentException
    {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("value is not an integer or out of range");
        }
    }
}
