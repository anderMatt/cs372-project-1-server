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
    private static final String HANDLE = "ANDERMA8>";

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

        System.out.println("Listening for connections...");
        while (this.isRunning) {
            client = server.accept();
            doChat();
            client.close();
        }
    }

    // The chat loop with another host.
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

    private String createMessage() throws IOException{
        System.out.print(HANDLE);
        String msg = keyboard.readLine();

        // Limit message length.
        if(msg.length() > MAX_MESSAGE_LENGTH) {
            msg = msg.substring(0, MAX_MESSAGE_LENGTH);
        }

        return msg;
    }

    private void sendMessage(String msg) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream());
        msg = HANDLE + msg;
        out.write(msg);
        out.close();
    }

    private String readMessage() throws IOException {
        String msg = clientInput.lines()
                .collect(Collectors.joining());
        System.out.println(msg);

        return msg;
    }

    private boolean isQuitSentinel(String candidate) {
        return candidate.trim().equals(QUIT_SENTINEL);
    }
}
