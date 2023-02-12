package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static final int PORT = 8090;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress())) {
            System.out.println("Server starting.....");
            while(true){
                Socket client = server.accept();
                System.out.println("Client connected to: " + client.getInetAddress());
                //Turn stream into scanner and writers
                Scanner readFromClient = new Scanner(client.getInputStream());
                PrintWriter sendToClient = new PrintWriter(client.getOutputStream(), true);

                while(readFromClient.hasNextLine()){
                    String nextLine = readFromClient.nextLine();
                    System.out.println("Remote client says: " + nextLine);
                    sendToClient.println("echo: " + nextLine);
                    //sendToClient.flush();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
