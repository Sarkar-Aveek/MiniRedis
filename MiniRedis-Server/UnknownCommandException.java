/**
 * Thrown when the client types a command MiniRedis does not know.
 */
public class UnknownCommandException extends RedisException
{
    public UnknownCommandException(String name)
    {
        super("ERR unknown command '" + name + "'");
    }
}
