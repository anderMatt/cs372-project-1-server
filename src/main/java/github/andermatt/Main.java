package github.andermatt;

import github.andermatt.chat.ChatServer;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        System.out.println("main running.");
        int port = getPortFromArgs(args);

        ChatServer server = new ChatServer();
        try {
            System.out.println("Starting server on... " + InetAddress.getLocalHost() + ":" + port);
            server.run(41000, InetAddress.getLocalHost());
        } catch (IOException ioex) {
            System.out.println("Error starting server: " + ioex);
        }
    }

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
