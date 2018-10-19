package github.andermatt.chat;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private final String QUIT_SENTINEL = "\\quit";
    private final int MAX_CONN_QUEUE = 25;
    private final String HANDLE = "ANDERMA8>";

    private boolean isRunning = false;
    private ServerSocket server;
    private Socket client;
    private BufferedReader clientInput;

    public ChatServer() {}

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
        boolean chatComplete = false;  // Either host may indicate completion with a quit.
        clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String msg;

        while (!chatComplete) {
            // Wait for message from client
            while ((msg = clientInput.readLine()) != null) {
                System.out.println("MESSAGE: " + msg);
                // TODO: check if quit
            }
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
