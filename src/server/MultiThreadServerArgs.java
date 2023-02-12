package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class MultiThreadServerArgs {
    //public static final int PORT = 8090;

    public static void main(String[] args) {
        final int PORT = parseInt(args[0]);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server starting.....");
            while(true){
                Socket client = server.accept();
                System.out.println("Client connected, starting new thread.");
                Thread newThread = new Thread(new ServerThread(client));
                newThread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}