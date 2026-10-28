import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * MiniRedis server. Run main(), then connect with RedisClient.
 * It listens on port 6379, the same port real Redis uses.
 */
public class RedisServer
{
    public static final int PORT = 6379;

    public static void main(String[] args)
    {
        Database db = new Database();
        CommandEngine engine = new CommandEngine(db);
        new ExpiryCleaner(db).start();

        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);   // throws BindException if the port is taken
            System.out.println("MiniRedis server started on port " + PORT);
            System.out.println("Waiting for clients...");

            int nextId = 1;
            while (true) {
                Socket client = server.accept();   // waits here until a client connects
                System.out.println("[server] client #" + nextId + " connected from "
                                   + client.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(client, engine, nextId)).start();
                nextId++;
            }
        } catch (BindException e) {
            System.out.println("ERROR: port " + PORT + " is already in use. Is the server already running?");
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            try {
                if (server != null) server.close();
            } catch (IOException e) {
                // ignore
            }
            System.out.println("MiniRedis server stopped");
        }
    }
}
