package floatilla;

import java.util.Objects;

public class PeerSocket {
    String hostname;
    int port;
    int responseTime;
    boolean failedConnection;

    public PeerSocket(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public PeerSocket() {
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
        return (obj instanceof PeerSocket) && this.equals(obj);
    }
}
