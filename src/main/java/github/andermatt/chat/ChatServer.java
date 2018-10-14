package github.andermatt.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private final String QUIT_SENTINEL = "\\quit";
    private final int MAX_CONN_QUEUE = 25;
    private final String HANDLE = "ANDERMA8 >";

    private boolean isRunning = false;
    private ServerSocket server;
    private Socket client;

    public ChatServer() {}

    // Main loop.
    public void run(int port, InetAddress bindAddress) throws IOException {
        if (this.isRunning) {
            throw new IllegalStateException("Server has already started.");
        }

        this.server = new ServerSocket(port, MAX_CONN_QUEUE, bindAddress);
        this.isRunning = true;

        while (true) {
            client = server.accept();
            doChat();
            client.close();
        }
    }

    // The chat loop with another host.
    private void doChat() {
        boolean chatComplete = false;  // Either host may indicate completion with a quit.

        while (!chatComplete) {
            // Wait for message from client
            // Check if client wants to quit
            // Generate response for client.
            // Check if user wants to quit.
            // Send response to client.
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

    private String createMessage() {
        // Allow user to create a message.
        return "";
    }

    private void sendMessage(String msg) {
        // Check if user wants to quit.
    }

    private void onMessageReceived(String msg) {
        // Check if client wants to end communication.
    }


}
