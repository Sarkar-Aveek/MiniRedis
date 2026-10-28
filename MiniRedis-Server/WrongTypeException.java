/**
 * Thrown when a command is used on the wrong kind of value, e.g. LPUSH on a string.
 */
public class WrongTypeException extends RedisException
{
    public WrongTypeException()
    {
        super("WRONGTYPE Operation against a key holding the wrong kind of value");
    }
}
