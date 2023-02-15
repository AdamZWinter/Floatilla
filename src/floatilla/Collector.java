package floatilla;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.security.PrivilegedExceptionAction;
import java.util.Date;
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
            // not a deep copy of the sockets, just the set of sockets
            Set<PeerSocket> deepishCopy = floatilla.deepCopyValidationSet();
            floatilla.clearValidationSet();
            Iterator<PeerSocket> currentlyValidating = deepishCopy.iterator();
            while(currentlyValidating.hasNext()){
                PeerSocket currentSocket = currentlyValidating.next();
                if(testSocket(currentSocket)){
                    floatilla.addValidatedSocket(currentSocket);
                    currentlyValidating.remove();
                }else{
                    currentlyValidating.remove();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

    public boolean testSocket(PeerSocket socket){
        System.out.println("Testing socket: "+socket.toString());
        BufferedReader br = null;
        StringBuilder strBuilder = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();

        if (socket.getProtocol().compareTo("http") == 0) {
            urlBuilder.append("http://");
        } else if(socket.getProtocol().compareTo("https") == 0){
            urlBuilder.append("https://");
        }else{
            System.out.println("Protocol not supported.");
            return false;
        }
        urlBuilder.append(socket.getHostname());
        if(socket.getPort() != 80 && socket.getPort() != 443 && socket.usePort()){
            urlBuilder.append(":");
            urlBuilder.append(socket.getPort());
        }
        if(socket.usePath()){
            urlBuilder.append(socket.getPath());
        }
        urlBuilder.append("?config=");
        urlBuilder.append(config.getHash());
        urlBuilder.append("&host=");
        urlBuilder.append(config.getMyHostname());
        if(config.useMyPort){
            urlBuilder.append("&port=");
            urlBuilder.append(config.getListeningPort());
        }else{
            urlBuilder.append("&port=");
            urlBuilder.append(0);
        }
        if(config.useMyPath){
            urlBuilder.append("&path=");
            urlBuilder.append(config.getUrlPath());
        }

        Date date = new Date();
        Long startTime = date.getTime();
        try {
            URL url = new URL(urlBuilder.toString());
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
                    strBuilder.append(strCurrentLine);
                    System.out.println(strCurrentLine);
                }
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    System.out.println(strCurrentLine);
                }
                return false;
            }

        } catch (SocketTimeoutException e){
            System.out.println("Connection timed out.");
            //handle this timeout by removing this socket from the Set
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Date dateEnd = new Date();
        Long endTime = dateEnd.getTime();
        int responseTime = (int) (endTime - startTime);
        socket.setResponseTime(responseTime);

        String response = strBuilder.toString();
        JSONObject jsonObject = new JSONObject(response);
        if(jsonObject.getInt("config") != config.getHash()){
            System.out.println("Configuration Mismatch!");
            return false;
        }

//        for (String key: jsonObject.keySet()) {
//            System.out.println("key: "+key + " Value: "+ jsonObject.get(key));
//        }



        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("peers"));
        if(jsonObject.getInt("numPeers") != jsonArray.length()){
            //Sanity check failed
            System.out.println("Peers reported " + jsonObject.getInt("numPeers")
                    + "but found array length: " + jsonArray.length());
            return false;
        }
        //System.out.println(jsonArray);
        if(!jsonArray.isEmpty()){
            //System.out.println("Index[0][0]: " + jsonArray.getJSONArray(0).getString(0));
            for(int i = 0; i < jsonArray.length(); i++){
                JSONArray singlePeerArray = jsonArray.getJSONArray(i);
                if(singlePeerArray.length() != 3 && singlePeerArray.length() != 4){
                    System.out.println("Error: single peer array length must be 3 or 4");
                    return false;
                }
                String protocol = singlePeerArray.getString(0);
                String hostname = singlePeerArray.getString(1);
                int port = singlePeerArray.getInt(2);
                //TODO:  Add validation
                PeerSocket peerSocket = new PeerSocket(protocol, hostname, port);

                if(singlePeerArray.length() == 4){
                    String path = singlePeerArray.getString(3);
                    peerSocket.setPath(path);
                }

                floatilla.queueForValidation(peerSocket);
            }
        }else{
            //return false; ???????
        }

        return true;

    }

}


