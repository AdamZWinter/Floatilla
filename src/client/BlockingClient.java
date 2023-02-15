package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BlockingClient {
    String text;

    public static void main(String[] args) {
        try(Socket server = new Socket("18.237.141.225", 8090)){
            //try(Socket server = new Socket("localhost", 8090)){

            //*****Connection is opened before console prompt******
            //Server is occupied with connection while it waits for you to type your message
            Scanner console = new Scanner(System.in);
            System.out.println("Enter text to send to server: ");
            String text = console.nextLine();

            Scanner fromServer = new Scanner(server.getInputStream());
            PrintWriter toServer = new PrintWriter(server.getOutputStream(), true);
            toServer.println(text);

            while(fromServer.hasNextLine()){
                System.out.println(fromServer.nextLine());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
