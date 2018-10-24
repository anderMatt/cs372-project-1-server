/*******************************************************************************
 * Matthew Anderson
 * 10/24/18
 * CS 372 Project 1
 *
 * Command-line chat client that listens on a given port for connections from
 * other chat clients. After a connection has been made, users are able to exchange
 * text messages.
 *******************************************************************************/


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

public class ChatServer {
    private static final String QUIT_SENTINEL = "\\quit";  // Value user may enter to end the chat.
    private static final int MAX_CONN_QUEUE = 25;
    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final String HANDLE = "ANDERMA8>";  // Prepended to all messages sent to client.

    private boolean isRunning = false;
    private ServerSocket server;  // Listens for connections.
    private Socket client;  // Sends and accepts messages to a connected client.
    private BufferedReader clientInput;
    private BufferedReader keyboard;  // Read in messages to send to connected client.

    public ChatServer() {
        keyboard = new BufferedReader(new InputStreamReader(System.in));
    }

    /*
    Main loop:
        - create server socket and listen on the given port
        - after a client has connected, engage in a chat until either party indicates they
        would like to end the chat
        - resume listening for connections from chat clients.
     */
    public void run(int port, InetAddress bindAddress) throws IOException {
        if (this.isRunning) {
            throw new IllegalStateException("Server has already started.");
        }


        this.server = new ServerSocket(port, MAX_CONN_QUEUE, bindAddress);
        this.isRunning = true;

        // Listen for incoming connections from chat clients, engage in chat, and resume listening
        // when a chat has ended.
        while (this.isRunning) {
            System.out.println("Listening for connections...");
            client = server.accept();
            doChat();
            client.close();
        }
        shutdown();
    }

    /*
    Chat loop with client. Follows this loop, until either user or client indicates desire to
    end the chat, by sending the quit sentinel string
    - receive message from client
    - check if client sent the quit sentinel, indicating a desire to end the chat. If so, terminate chat
    - print message
    - read in message from keyboard, and send message to client
    - check if out user wants to end the chat (entered quit sentinel). If so, terminate chat
     */
    private void doChat() throws IOException{
        clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String receivedMessage;
        String messageToSend;

        while (true) {
            // Wait for message from client
            receivedMessage = readMessage();

            // Check if client wants to end the chat.
            if (isQuitSentinel(receivedMessage)) {
                System.out.println("Client ended chat.");
                break;
            }
            System.out.println(receivedMessage);

            messageToSend = createMessage();
            sendMessage(messageToSend);

            // Do this after sending to client, to inform it we want
            // to end the chat.
            if (isQuitSentinel(messageToSend)) {
                System.out.println("Ending chat.");
                break;
            }
        }
    }

    /*
    Resource cleanup.
    */
    public void shutdown() {
        if (!isRunning) {
            throw new IllegalStateException("Server is not running.");
        }

        System.out.println("Shutting down...");
        isRunning = false;

        if (this.server != null) {
            try {
                this.server.close();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
        }
    }

    /*
    Create message from keyboard input to send to client. Chat will be
    terminated if quit sentinel is entered.
     */
    private String createMessage() throws IOException{
        System.out.print(HANDLE);
        String msg = keyboard.readLine();

        // Limit message length.
        if(msg.length() > MAX_MESSAGE_LENGTH) {
            msg = msg.substring(0, MAX_MESSAGE_LENGTH);
        }

        return msg;
    }

    /*
    Sends 'msg' to the client.
     */
    private void sendMessage(String msg) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream());
        msg = HANDLE + msg;
        out.write(msg);  // TODO: add '\n'?
        out.flush();
    }

    /*
    Prints message received from client.
     */
    private String readMessage() throws IOException{
        String msg = clientInput.readLine();

        return msg;
    }

    /*
    Checks if the passed string is the quit sentinel. When checking messages
    received from the client, the client's user handle needs to be removed before
    evaluating the text.
     */
    private boolean isQuitSentinel(String candidate) {
        // Need to remove the clients handle to evaluate the message text.
        int handleIndex = candidate.indexOf('>');  // {username}>{msg}
        if (handleIndex > -1) {
            candidate = candidate.substring(handleIndex + 1);
        }
        return candidate.trim().equals(QUIT_SENTINEL);
    }
}
