package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MathServer {
    public static final int PORT = 8090;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress())) {
            System.out.println("Server starting.....");
            while(true){
                Socket client = server.accept();
                System.out.println("Client connected, starting new thread.");
                Thread newThread = new Thread(new MathThread(client));
                newThread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
