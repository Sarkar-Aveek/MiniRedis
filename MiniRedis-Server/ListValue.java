import java.util.ArrayList;

/**
 * A list of strings, created by LPUSH.
 */
public class ListValue extends RedisValue
{
    private ArrayList<String> items = new ArrayList<String>();

    public String type()
    {
        return "list";
    }

    public void pushFront(String item)
    {
        items.add(0, item);
    }

    public int size()
    {
        return items.size();
    }

    // Like Redis LRANGE: stop is inclusive, negative indexes count from the end (-1 = last)
    public ArrayList<String> range(int start, int stop)
    {
        if (start < 0) start = items.size() + start;
        if (stop < 0)  stop  = items.size() + stop;
        if (start < 0) start = 0;
        if (stop >= items.size()) stop = items.size() - 1;

        ArrayList<String> result = new ArrayList<String>();
        for (int i = start; i <= stop; i++) {
            result.add(items.get(i));
        }
        return result;
    }
}
