/*******************************************************************************
 * Matthew Anderson
 * 10/24/18
 * CS 372 Project 1
 *
 * Command-line chat client that listens on a given port for connections from
 * other chat clients. After a connection has been made, users are able to exchange
 * text messages.
 *******************************************************************************/
import java.io.IOException;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        int port = getPortFromArgs(args);

        ChatServer server = new ChatServer();
        try {
            System.out.println("Starting server on " + InetAddress.getLocalHost() + ":" + port);
            server.run(port, InetAddress.getLocalHost());
        } catch (IOException ioex) {
            System.out.println("Error starting server: " + ioex);
        }
    }

    /*
    Parse port to listen on from command line arguments. If not provided,
    or invalid, exit with an error.
     */
    private static int getPortFromArgs(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: main port");
            System.exit(1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch(NumberFormatException nfe) {
            System.out.println("Port must be an integer.");
            System.exit(1);
        }
        return port;
    }
}
