/**
 * Base class for every value stored in MiniRedis.
 * It is abstract, so only concrete types (StringValue, ListValue...) can be stored.
 */
public abstract class RedisValue
{
    // Every subclass must say what type it is: "string", "list", ...
    public abstract String type();

    // final: no subclass may change this rule.
    // Commands call it before using a value, e.g. LPUSH checks for "list".
    public final void checkType(String expected) throws WrongTypeException
    {
        if (!type().equals(expected)) {
            throw new WrongTypeException();
        }
    }
}
