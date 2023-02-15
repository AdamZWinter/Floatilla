package server;

import floatilla.Collector;
import floatilla.Floatilla;
import floatilla.FloatillaConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SimpleWebServer waits to accept a client Socket
 * Then hands off that client Socket to the WebService and waits for another
 */
public class Main {
    //public static final int PORT = 8090;;

    /**
     * Main
     * @param args not used
     */
    public static void main(String[] args) {
        FloatillaConfig config = new FloatillaConfig("config.json");
        Floatilla floatilla = new Floatilla(config);
        Thread collectorThread = new Thread(new Collector(floatilla));
        collectorThread.start();

        //try (ServerSocket server = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress())) {
        try (ServerSocket server = new ServerSocket(config.getListeningPort())) {
            System.out.println("Server starting.....");
            for(;;){
                Socket client = server.accept();
                System.out.println("Client connected, starting new thread.");
                Thread newThread = new Thread(new WebService(client, floatilla));
                newThread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
