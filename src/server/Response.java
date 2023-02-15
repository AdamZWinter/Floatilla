package server;

import floatilla.FloatillaConfig;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Response implements Serializable {
    public int config;
    public int numPeers;
    public Set<SimpleSocket> peers;

    public Response(FloatillaConfig config){
        this.config = config.getHash();
        this.peers = new HashSet<>();
        this.numPeers = 0;
    }

    public void addSocket(String hostname, int port){
        peers.add(new SimpleSocket(hostname, port));
        this.numPeers = peers.size();
    }

    public class SimpleSocket implements Serializable {
        public String hostname;
        public int port;

        public SimpleSocket(String hostname, int port){
            this.hostname = hostname;
            this.port = port;
        }
    }
}
