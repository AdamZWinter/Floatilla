package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread implements Runnable{

    Socket client;

    public ServerThread(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Client connected from: " + client.getInetAddress() + "  on thread: " + Thread.currentThread().getName());
        //Turn stream into scanner and writers
        Scanner readFromClient = null;
        try {
            readFromClient = new Scanner(client.getInputStream());
            PrintWriter sendToClient = new PrintWriter(client.getOutputStream(), true);

            while(readFromClient.hasNextLine()){
                String nextLine = readFromClient.nextLine();
                System.out.println("Remote client says: " + nextLine);
                sendToClient.println("Received message: " + nextLine);
                sendToClient.println("Thank you, come again.");
                sendToClient.flush();
                sendToClient.close();
                //break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
