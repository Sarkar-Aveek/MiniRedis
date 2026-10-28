/** LPUSH key item [item ...]  ->  adds items to the front of a list, returns the new length */
public class LPushCommand extends Command
{
    public LPushCommand()
    {
        super("LPUSH", 2, -1);
    }

    protected String execute(Database db, String[] args) throws RedisException
    {
        RedisValue value = db.get(args[0]);
        if (value == null) {
            value = new ListValue();
            db.set(args[0], value);
        }
        value.checkType("list");

        ListValue list = (ListValue) value;
        for (int i = 1; i < args.length; i++) {
            list.pushFront(args[i]);
        }
        return integer(list.size());
    }
}
