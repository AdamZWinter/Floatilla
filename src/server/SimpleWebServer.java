package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SimpleWebServer waits to accept a client Socket
 * Then hands off that client Socket to the WebService and waits for another
 */
public class SimpleWebServer {
    public static final int PORT = 8090;
    //public static final String WEB_ROOT = "H:\\school\\SDEV301Fall2022\\html";
    //test

    /**
     * Main
     * @param args not used
     */
    public static void main(String[] args) {
        //try (ServerSocket server = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress())) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server starting.....");
            for(;;){
                Socket client = server.accept();
                System.out.println("Client connected, starting new thread.");
                Thread newThread = new Thread(new WebService(client));
                newThread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
