package floatilla;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Collector implements Runnable {

    Floatilla floatilla;
    FloatillaConfig config;

    //seeds are added to validation set upon construction of Floatilla
    //seeds are only added to validated set through the test process below
    //seeds are to be removed after the first round
    public Collector(Floatilla floatilla) {
        this.floatilla = floatilla;
        this.config = floatilla.getConfig();
    }

    @Override
    public void run() {
        System.out.println("Collector running.");
        int round = 1;                      //Things to do/not on/after the first round through this loop
        while(true){
            System.out.println("---------------------------------------------------------------------");
            System.out.println("Start Round:");
            // not a deep copy of the sockets, just the set of sockets
            Set<PeerSocket> shallowCopy = floatilla.shallowCopyValidationSet();
            floatilla.clearValidationSet();
            Iterator<PeerSocket> currentlyValidating = shallowCopy.iterator();
            System.out.println(currentlyValidating.hasNext());
            while(currentlyValidating.hasNext()){
                PeerSocket currentSocket = currentlyValidating.next();
                if(!floatilla.contains(currentSocket) && !floatilla.isBlackListed(currentSocket)){
                    if(testSocket(currentSocket)){
                        floatilla.addValidatedSocket(currentSocket);
                        //currentlyValidating.remove();
                    }else{
                        //currentlyValidating.remove();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(round == 1){
                floatilla.removeSeeds();
            }

            if(round == 101){
                System.out.println("100 collection rounds completed");
                floatilla.stageReValidation();
                round = 2;
            }

            if(round > 2 && floatilla.hasEmptyValidationSet()){
                System.out.println("No new sockets to validate.  Sleeping....");
                System.out.println("Validated Set: "+ floatilla);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            round++;

        }
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

//                System.out.println(peerSocket);
//                System.out.println("Queueing for validation:" + peerSocket);
//                floatilla.queueForValidation(peerSocket);

                //this checks the third-party sockets returned by the socket being tested
                //if the third-party sockets are ones we have already validated or are blacklisted
                //then we do not add them to the validation set (these will not be further validated)
                if(!floatilla.contains(peerSocket) && !floatilla.isBlackListed(peerSocket)){
                    System.out.println("Queueing for validation:" + peerSocket);
                    floatilla.queueForValidation(peerSocket);
                }else{
                    System.out.println("Socket already validated, or blacklisted.");
                    floatilla.queueForValidation(peerSocket);
                }

            }
        }else{
            //return false; ???????
        }

        return true;

    }

}


