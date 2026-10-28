/**
 * Thrown for bad arguments: wrong count, or text where a number was expected.
 */
public class InvalidArgumentException extends RedisException
{
    public InvalidArgumentException(String message)
    {
        super("ERR " + message);
    }
}
