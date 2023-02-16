package floatilla;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FloatillaConfig {

    private String myProtocol;
    private String myHostname;
    private int listeningPort;
    private String urlPath;
    boolean useMyPort;
    boolean useMyPath;
    private int peerCountLimit;
    private int maxResponseTime;
    private Set<PeerSocket> seeds;
    private Set<String> rootCertAuthorities;  //placeholder
    private int maxCertChainLength;
    private int revalidationRounds;



    public FloatillaConfig(String filename){
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Scanner fileIn = new Scanner(new File(filename));
            //fileIn.useDelimiter("");
            while(fileIn.hasNextLine()){
                stringBuilder.append(fileIn.nextLine());
            }//end while
        } catch (FileNotFoundException e) {
            System.out.println("File not found. ");
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        this.myProtocol = jsonObject.getString("myProtocol");
        this.myHostname = jsonObject.getString("myHostname");
        this.listeningPort = jsonObject.getInt("listeningPort");
        this.urlPath = jsonObject.getString("urlPath");
        this.useMyPort = jsonObject.getBoolean("useMyPort");
        this.useMyPath = jsonObject.getBoolean("useMyPath");
        this.peerCountLimit = jsonObject.getInt("peerCountLimit");
        this.maxResponseTime = jsonObject.getInt("maxResponseTime");

        this.seeds = new HashSet<>();
        JSONArray jsonArraySeeds = jsonObject.getJSONArray("seeds");
        for(Object seed : jsonArraySeeds){
            //System.out.println(seed);
            JSONObject jsonObjectSeed = new JSONObject(seed.toString());
            PeerSocket peerSocket = new PeerSocket();
            peerSocket.setProtocol(jsonObjectSeed.getString("protocol"));
            peerSocket.setHostname(jsonObjectSeed.getString("hostname"));
            peerSocket.setPort(jsonObjectSeed.getInt("port"));
            if(jsonObjectSeed.keySet().contains("path")){
                peerSocket.setPath(jsonObjectSeed.getString("path"));
            }
            this.seeds.add(peerSocket);
        }


        this.rootCertAuthorities = new HashSet<>();
        JSONArray jsonArray = jsonObject.getJSONArray("rootCertAuthorities");
        for (Object certAuth : jsonArray) {
            this.rootCertAuthorities.add((String)certAuth);
        }

        this.maxCertChainLength = jsonObject.getInt("maxCertChainLength");
        this.revalidationRounds = jsonObject.getInt("revalidationRounds");

    }


    public String getMyProtocol() {
        return myProtocol;
    }

    public void setMyProtocol(String myProtocol) {
        this.myProtocol = myProtocol;
    }

    public String getMyHostname() {
        return myHostname;
    }

    public void setMyHostname(String myHostname) {
        this.myHostname = myHostname;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }
    public String getUrlPath(){return urlPath;}

    public int getPeerCountLimit() {
        return peerCountLimit;
    }

    public void setPeerCountLimit(int peerCountLimit) {
        this.peerCountLimit = peerCountLimit;
    }

    public Set<PeerSocket> getSeeds() {
        return seeds;
    }

    public void setSeeds(Set<PeerSocket> seeds) {
        this.seeds = seeds;
    }

    public Set<String> getRootCertAuthorities(){return rootCertAuthorities;}

    public Integer getHash(){
        return Objects.hash(urlPath, rootCertAuthorities, maxCertChainLength);
    }

    public int getRevalidationRounds() {
        return revalidationRounds;
    }
}
