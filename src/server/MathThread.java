package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathThread implements Runnable{

    Socket client;

    public MathThread(Socket client){
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
                String[] stringArray = nextLine.split(" ");
                if(stringArray.length != 2){
                    sendToClient.println("Math protocol expects: <num1> <num2>");
                    sendToClient.close();
                }
                Pattern p = Pattern.compile("\\D");
                Matcher m1 = p.matcher(stringArray[0]);
                Matcher m2 = p.matcher(stringArray[1]);
                if(m1.matches() || m2.matches()){
                    sendToClient.println("Math protocol expects: <num1> <num2>");
                    sendToClient.close();
                }

                int num1 = Integer.parseInt(stringArray[0]);
                int num2 = Integer.parseInt(stringArray[1]);

                try {
                    sendToClient.println( num1 + " + " + num2 + " = " + (num1 + num2) );
                    sendToClient.println( num1 + " - " + num2 + " = " + (num1 - num2) );
                    sendToClient.println( num1 + " * " + num2 + " = " + (num1 * num2) );
                    sendToClient.println( num1 + " / " + num2 + " = " + (num1 / num2) );
                    sendToClient.println( num1 + " % " + num2 + " = " + (num1 % num2) );
                } catch (Exception e) {
                    sendToClient.println("Math protocol expects: <num1> <num2>");
                    sendToClient.close();
                }

                sendToClient.flush();
                sendToClient.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
