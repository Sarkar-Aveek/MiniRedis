import java.util.ArrayList;
import java.util.Arrays;

/** KEYS pattern  ->  "*" for all keys, "user*" for keys starting with "user" */
public class KeysCommand extends Command
{
    public KeysCommand()
    {
        super("KEYS", 1, 1);
    }

    protected String execute(Database db, String[] args)
    {
        String pattern = args[0];
        String[] all = db.keys();
        Arrays.sort(all);   // sorted so the output is easy to read

        ArrayList<String> matches = new ArrayList<String>();
        for (String key : all) {
            boolean match;
            if (pattern.endsWith("*")) {
                match = key.startsWith(pattern.substring(0, pattern.length() - 1));
            } else {
                match = key.equals(pattern);
            }
            if (match) {
                matches.add(key);
            }
        }
        return list(matches);
    }
}
