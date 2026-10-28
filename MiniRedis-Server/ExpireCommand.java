/** EXPIRE key seconds  ->  1 if the timer was set, 0 if the key does not exist */
public class ExpireCommand extends Command
{
    public ExpireCommand()
    {
        super("EXPIRE", 2, 2);
    }

    protected String execute(Database db, String[] args) throws RedisException
    {
        long seconds = parseNumber(args[1]);
        return integer(db.expire(args[0], seconds) ? 1 : 0);
    }
}
