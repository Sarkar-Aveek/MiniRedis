import java.util.Arrays;
import java.util.HashMap;

/**
 * Turns one line of text ("SET name Aveek") into a reply ("OK").
 * It knows nothing about sockets, so the same engine works for any input.
 */
public class CommandEngine
{
    private Database db;
    private HashMap<String, Command> commands = new HashMap<String, Command>();

    public CommandEngine(Database db)
    {
        this.db = db;
        register(new PingCommand());
        register(new SetCommand());
        register(new GetCommand());
        register(new DelCommand());
        register(new IncrCommand());
        register(new LPushCommand());
        register(new LRangeCommand());
        register(new KeysCommand());
        register(new ExpireCommand());
        register(new TtlCommand());
    }

    private void register(Command c)
    {
        commands.put(c.getName(), c);   // superclass reference holds any subclass
    }

    public String execute(String line)
    {
        String[] parts = line.trim().split("\\s+");
        String name = parts[0].toUpperCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        try {
            Command command = commands.get(name);
            if (command == null) {
                throw new UnknownCommandException(parts[0]);
            }
            // Lock the whole database while one command runs, so a command like
            // INCR (read, add 1, write) can never be mixed with another thread's command.
            synchronized (db) {
                return command.run(db, args);
            }
        } catch (RedisException e) {
            return "(error) " + e.getMessage();
        }
    }
}
