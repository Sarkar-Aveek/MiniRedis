import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Talks to ONE connected client. The server runs each ClientHandler on its own thread,
 * so many clients can be served at the same time.
 */
public class ClientHandler implements Runnable
{
    private Socket socket;
    private CommandEngine engine;
    private int id;

    public ClientHandler(Socket socket, CommandEngine engine, int id)
    {
        this.socket = socket;
        this.engine = engine;
        this.id = id;
    }

    public void run()
    {
        try {
            // Read text lines from the client, write text lines back
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {   // null = client closed the connection
                if (line.trim().isEmpty()) {
                    continue;
                }
                System.out.println("[client #" + id + "] " + line);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK");
                    out.println();
                    break;
                }
                out.println(engine.execute(line));
                out.println();   // a blank line marks the end of the reply
            }
        } catch (IOException e) {
            System.out.println("[client #" + id + "] connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // nothing more we can do
            }
            System.out.println("[server] client #" + id + " disconnected");
        }
    }
}
