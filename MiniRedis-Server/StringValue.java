/**
 * A plain text value, created by SET.
 */
public class StringValue extends RedisValue
{
    protected String text;   // protected: subclasses can see it

    public StringValue(String text)
    {
        this.text = text;
    }

    public String type()
    {
        return "string";
    }

    public String get()
    {
        return text;
    }
}
