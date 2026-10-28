/** DEL key [key ...]  ->  how many keys were deleted */
public class DelCommand extends Command
{
    public DelCommand()
    {
        super("DEL", 1, -1);
    }

    protected String execute(Database db, String[] args)
    {
        int deleted = 0;
        for (int i = 0; i < args.length; i++) {
            if (db.delete(args[i])) {
                deleted++;
            }
        }
        return integer(deleted);
    }
}
