/** GET key */
public class GetCommand extends Command
{
    public GetCommand()
    {
        super("GET", 1, 1);
    }

    protected String execute(Database db, String[] args) throws RedisException
    {
        RedisValue value = db.get(args[0]);
        if (value == null) {
            return text(null);
        }
        value.checkType("string");
        // value may really be a CounterValue: get() then runs the overridden version
        return text(((StringValue) value).get());
    }
}
