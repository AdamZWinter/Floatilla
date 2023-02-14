package floatilla;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FloatillaConfig {

    private String myHostname;
    private int listeningPort;

    //for local test environment
    private boolean secureConnections;
    private int peerCountLimit;
    private int maxResponseTime;
    private Set<PeerSocket> seeds;
    private Set<String> rootCertAuthorities;  //placeholder
    private int maxCertChainLength;



    public FloatillaConfig(String filename){
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Scanner fileIn = new Scanner(new File("config.json"));
            //fileIn.useDelimiter("");
            while(fileIn.hasNextLine()){
                stringBuilder.append(fileIn.nextLine());
            }//end while
        } catch (FileNotFoundException e) {
            System.out.println("File not found. ");
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        this.myHostname = jsonObject.getString("myHostname");
        this.listeningPort = jsonObject.getInt("listeningPort");
        this.secureConnections = jsonObject.getBoolean("secureConnections");
        this.peerCountLimit = jsonObject.getInt("peerCountLimit");
        this.maxResponseTime = jsonObject.getInt("maxResponseTime");

        this.seeds = new HashSet<>();
        JSONArray jsonArraySeeds = jsonObject.getJSONArray("seeds");
        for(Object seed : jsonArraySeeds){
            //System.out.println(seed);
            JSONObject jsonObjectSeed = new JSONObject(seed.toString());
            PeerSocket peerSocket = new PeerSocket(jsonObjectSeed.getString("hostname"), jsonObjectSeed.getInt("port"));
            this.seeds.add(peerSocket);
        }


        this.rootCertAuthorities = new HashSet<>();
        JSONArray jsonArray = jsonObject.getJSONArray("rootCertAuthorities");
        //List<String> rootCertAuthArray = new ArrayList<>();
        for (Object certAuth : jsonArray) {
            this.rootCertAuthorities.add((String)certAuth);
        }

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

    public boolean useSecureConnections(){
        return secureConnections;
    }

    public Set<String> getRootCertAuthorities(){return rootCertAuthorities;}
}
