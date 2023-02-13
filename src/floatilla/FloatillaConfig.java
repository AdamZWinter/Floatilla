package floatilla;

import java.util.Set;

public class FloatillaConfig {

    private String myHostname;
    private int listeningPort;
    private int peerCountLimit;
    private int maxResponseTime;
    private Set<PeerSocket> seeds;

    //for local test environment
    private boolean secureConnections;


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
}
