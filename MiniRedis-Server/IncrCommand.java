/** INCR key  ->  adds 1 to a number (a missing key starts at 0) */
public class IncrCommand extends Command
{
    public IncrCommand()
    {
        super("INCR", 1, 1);
    }

    protected String execute(Database db, String[] args) throws RedisException
    {
        RedisValue value = db.get(args[0]);
        if (value == null) {
            value = new CounterValue(0);
            db.set(args[0], value);
        }
        value.checkType("string");
        if (!(value instanceof CounterValue)) {
            throw new InvalidArgumentException("value is not an integer or out of range");
        }
        return integer(((CounterValue) value).incr());
    }
}
