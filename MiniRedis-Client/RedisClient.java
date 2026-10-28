import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

/**
 * A small redis-cli: reads commands from the keyboard, sends them to the
 * MiniRedis server and prints the replies. Type QUIT to leave.
 */
public class RedisClient
{
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 6379);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to MiniRedis at 127.0.0.1:6379");

            while (true) {
                System.out.print("127.0.0.1:6379> ");
                if (!keyboard.hasNextLine()) {
                    break;
                }
                String command = keyboard.nextLine().trim();
                if (command.isEmpty()) {
                    continue;
                }
                out.println(command);

                // Print reply lines until the blank line that ends the reply
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                }
                if (line == null) {
                    System.out.println("Server closed the connection");
                    break;
                }
                if (command.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (ConnectException e) {
            System.out.println("Could not connect to MiniRedis at 127.0.0.1:6379: Connection refused");
            System.out.println("(Start RedisServer first.)");
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                // ignore
            }
            keyboard.close();
            System.out.println("Bye!");
        }
    }
}
