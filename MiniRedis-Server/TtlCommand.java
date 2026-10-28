/** TTL key  ->  seconds left, -1 if it never expires, -2 if it does not exist */
public class TtlCommand extends Command
{
    public TtlCommand()
    {
        super("TTL", 1, 1);
    }

    protected String execute(Database db, String[] args)
    {
        return integer(db.ttl(args[0]));
    }
}
