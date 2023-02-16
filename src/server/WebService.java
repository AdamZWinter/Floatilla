package server;

import floatilla.Floatilla;
import floatilla.PeerSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class WebService implements Runnable{

    Socket client;
    Floatilla floatilla;
    Scanner readFromClient;
    PrintWriter sendToClient;

    /**
     * Constructor sets the client Socket
     * @param client Socket
     */
    public WebService(Socket client, Floatilla floatilla){

        this.client = client;
        this.floatilla = floatilla;

    }

    /**
     * creates a Scanner and PrinterWriter from the client
     * Sends status headers and reads request headers
     * Currently only handles GET requests
     * Returns the lines of the file specified in the GET request
     */
    @Override
    public void run() {
        System.out.println("Client connected from: " + client.getInetAddress() + "  on thread: " + Thread.currentThread().getName());
        try {
            this.readFromClient = new Scanner(client.getInputStream());
            this.sendToClient = new PrintWriter(client.getOutputStream());

            sendToClient.println("HTTP/1.1 200");
            sendToClient.println("Content-Type: text/html");
            sendToClient.println("Connection: close");
            sendToClient.println("");

            List<String> requestHeaders = new ArrayList<>();
            while(readFromClient.hasNextLine()){
                String nextLine = readFromClient.nextLine();
                System.out.println(nextLine);
                if(nextLine.length() == 0){
                    break;
                }else{
                    requestHeaders.add(nextLine);
                }
            }

            if(requestHeaders.size() == 0){
                System.out.println("request has no headers"+System.lineSeparator());
                sendToClient.close();
                readFromClient.close();
                client.close();
                //System.exit(1);
                Thread.currentThread().interrupt();
                return;
            }

            String[] argArray = requestHeaders.get(0).split(" ");
            if(argArray[0].compareTo("GET") == 0){
                //TODO: Verify config argument
                //TODO: Read arguments and add peer to validation set
                PeerSocket peerSocket = null;
                peerSocket = parseQuery(argArray[1]);
                if(peerSocket == null){
                    //peer node has not correctly identified itself
                    //or peer node has configuration mismatch
                    System.out.println("Request from peer socket is malformed.");
                    sendToClient.close();
                    readFromClient.close();
                    client.close();
                    Thread.currentThread().interrupt();
                    return;
                }else{
                    System.out.println("Queueing peer for validation by request: " + peerSocket);
                    floatilla.queueForValidation(peerSocket);
                    sendResponse("GET");
                }
            }else{
                System.out.println("Did not receive GET request.");
                sendToClient.close();
                readFromClient.close();
                client.close();
                Thread.currentThread().interrupt();
                return;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(String command) throws IOException {
        //floatilla.fakeValidateAll();
        Iterator<PeerSocket> flitr = floatilla.getValidatedIterator();
        //Map<String, Integer> socketMap = new HashMap<>();
        JSONArray peersArray = new JSONArray();
        while (flitr.hasNext()){
            PeerSocket socket = flitr.next();
            JSONArray jsonArray = new JSONArray();
            //JSONObject socketObject = new JSONObject();
            jsonArray.put(0, socket.getProtocol());
            jsonArray.put(1, socket.getHostname());
            jsonArray.put(2, socket.getPort());
            if(socket.usePath()){
                jsonArray.put(3, socket.getPath());
            }

            peersArray.put(jsonArray);

            //socketMap.put(socket.getHostname(), socket.getPort());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("config", floatilla.getConfig().getHash());
        jsonObject.put("numPeers", peersArray.length());
        jsonObject.put("peers", peersArray);

        sendToClient.println(jsonObject.toString());

        sendToClient.flush();
        sendToClient.close();
        readFromClient.close();
        client.close();
    }

    // assumes you are parsing a line that looks like:
    // /path/resource?key=value&parameter=value
    // which you got from a request header line that looks like this:
    // GET /path/resource?key=value&parameter=value HTTP/1.1
    private PeerSocket parseQuery(String path){
        if(path == null || path.isEmpty()){         //basic sanity check
            return null;
        }
        int indexOfQ = path.indexOf("?");           //where the query string starts
        if(indexOfQ == -1){return  null;}
        String queryString = path.substring(indexOfQ + 1);
        String[] queryStringArray = queryString.split("&");
        Map<String, String> kvMap = new HashMap<>();
        for(String kvString : queryStringArray){
            int indexOfE = kvString.indexOf("=");
            if(indexOfE == -1 || indexOfE == 0){return  null;}
            String[] kvPairArray = kvString.split("=");
            kvMap.put(kvPairArray[0], kvPairArray[1]);
        }
        if(Integer.parseInt(kvMap.get("config")) != floatilla.getConfig().getHash()){
            System.out.println("Config mismatch from peer query.");
            return null;
        }
        PeerSocket peerSocket = new PeerSocket();
        peerSocket.setProtocol(kvMap.get("protocol"));
        peerSocket.setHostname(kvMap.get("hostname"));
        peerSocket.setPort(Integer.parseInt(kvMap.get("port")));
        if(kvMap.containsKey("path")){
            peerSocket.setPath(kvMap.get("path"));
        }

        return peerSocket;
    }
}

