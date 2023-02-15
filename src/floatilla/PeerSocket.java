package floatilla;

import java.util.Objects;

public class PeerSocket {
    String protocol;
    String hostname;
    int port;
    String path;    //starts with forward slash /
    int responseTime;
    boolean failedConnection;

    public PeerSocket(String protocol, String hostname, int port) {
        this.protocol = protocol;
        this.hostname = hostname;
        this.port = port;
    }

    public PeerSocket() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    //path string should start with a forward slash
    //and end on the resource (not a directory, does not end with slash)
    public void setPath(String path) {
        this.path = path;
    }

    public boolean usePort(){
        return port != 0;
    }

    public boolean usePath(){
        return path != null;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public boolean isFailedConnection() {
        return failedConnection;
    }

    public void setFailedConnection(boolean failedConnection) {
        this.failedConnection = failedConnection;
    }

    @Override
    public String toString() {
        return "[" + hostname + ", " + port + ']';
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, port);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PeerSocket) && this.hashCode() == obj.hashCode();
    }
}
