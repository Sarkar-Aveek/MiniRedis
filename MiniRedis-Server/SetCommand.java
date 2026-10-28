/** SET key value */
public class SetCommand extends Command
{
    public SetCommand()
    {
        super("SET", 2, 2);
    }

    protected String execute(Database db, String[] args)
    {
        String value = args[1];
        RedisValue stored;
        try {
            // A whole number (like "10", but not "010") is stored as a CounterValue so INCR works
            long n = Long.parseLong(value);
            if (String.valueOf(n).equals(value)) {
                stored = new CounterValue(n);
            } else {
                stored = new StringValue(value);
            }
        } catch (NumberFormatException e) {
            stored = new StringValue(value);
        }
        db.set(args[0], stored);
        return "OK";
    }
}
