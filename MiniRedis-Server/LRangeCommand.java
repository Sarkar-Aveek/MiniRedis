import java.util.ArrayList;

/** LRANGE key start stop  ->  e.g. LRANGE tasks 0 -1 shows the whole list */
public class LRangeCommand extends Command
{
    public LRangeCommand()
    {
        super("LRANGE", 3, 3);
    }

    protected String execute(Database db, String[] args) throws RedisException
    {
        int start = (int) parseNumber(args[1]);
        int stop = (int) parseNumber(args[2]);

        RedisValue value = db.get(args[0]);
        if (value == null) {
            return list(new ArrayList<String>());
        }
        value.checkType("list");
        return list(((ListValue) value).range(start, stop));
    }
}
