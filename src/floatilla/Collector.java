package floatilla;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Collector implements Runnable {

    Floatilla floatilla;
    FloatillaConfig config;

    public Collector(Floatilla floatilla) {
        this.floatilla = floatilla;
        this.config = floatilla.getConfig();
    }

    @Override
    public void run() {
        System.out.println("Collector running.");
        floatilla.stageReValidation();
        while(true){
            Iterator<PeerSocket> currentlyValidating = floatilla.getValidatingIterator();
            while(currentlyValidating.hasNext()){
                PeerSocket currentSocket = currentlyValidating.next();
                testSocket(currentSocket);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }



        //Scanner console = new Scanner(System.in);
        //String text = console.nextLine();
        //try(Socket server = new Socket("18.237.141.225", 8090)){
//        try(Socket server = new Socket("localhost", 8090)){
//
//            Scanner fromServer = new Scanner(server.getInputStream());
//            PrintWriter toServer = new PrintWriter(server.getOutputStream(), true);
//            toServer.println(text);
//
//            while(fromServer.hasNextLine()){
//                System.out.println(fromServer.nextLine());
//            }
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void validateSockets(){
//        Set<PeerSocket> currentlyValidating = floatilla.;
//        for (PeerSocket socket: currentlyValidating
//        ) {
//            testSocket(socket);
//        }
    }

    public void testSocket(PeerSocket socket){
        System.out.println("Testing socket: "+socket.toString());
        BufferedReader br = null;
        try {
            if (!config.useSecureConnections()) {
                URL url = new URL("http://" + socket.getHostname() + ":" + socket.getPort()+"/floatillaTestChannel?host=" + config.getMyHostname() + "&port=" + config.getListeningPort());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                //con.setDoOutput(true);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                //DataOutputStream out = new DataOutputStream(con.getOutputStream());
                //out.writeBytes("/floatillaTestChannel?host=" + config.getMyHostname() + "&port=" + config.getListeningPort());
                //out.flush();
                //out.close();

                if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null) {
                        System.out.println(strCurrentLine);
                    }
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null) {
                        System.out.println(strCurrentLine);
                    }
                }
            } else {
                //use https
            }

        } catch (SocketTimeoutException e){
            System.out.println("Connection timed out.");
            //handle this timeout by removing this socket from the Set
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


