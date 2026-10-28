/** PING [message]  ->  PONG, or echoes the message back */
public class PingCommand extends Command
{
    public PingCommand()
    {
        super("PING", 0, 1);
    }

    protected String execute(Database db, String[] args)
    {
        return args.length == 0 ? "PONG" : text(args[0]);
    }
}
