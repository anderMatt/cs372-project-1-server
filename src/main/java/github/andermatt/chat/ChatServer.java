package github.andermatt.chat;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

public class ChatServer {
    private static final String QUIT_SENTINEL = "\\quit";
    private static final int MAX_CONN_QUEUE = 25;
    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final String HANDLE = "ANDERMA8>";  // Prepended to all messages sent to client.

    private boolean isRunning = false;
    private ServerSocket server;
    private Socket client;
    private BufferedReader clientInput;
    private BufferedReader keyboard;

    public ChatServer() {
        keyboard = new BufferedReader(new InputStreamReader(System.in));
    }

    // Main loop.
    public void run(int port, InetAddress bindAddress) throws IOException {
        if (this.isRunning) {
            throw new IllegalStateException("Server has already started.");
        }

        this.server = new ServerSocket(port, MAX_CONN_QUEUE, bindAddress);
        this.isRunning = true;

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
    end the chat, by sending the quit sentinel string.
    - Receive message from client
    - Send message to client
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
        // TODO: possible to be multiple lines? Is this cutting off messages?
        String msg = clientInput.readLine();
        System.out.println(msg);

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
