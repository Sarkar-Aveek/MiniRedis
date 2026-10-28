/**
 * Parent of all MiniRedis errors. It extends Exception, so it is a checked exception.
 * The message is what the client sees after "(error)".
 */
public class RedisException extends Exception
{
    public RedisException(String message)
    {
        super(message);
    }
}
