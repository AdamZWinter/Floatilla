package floatilla;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Floatilla {

    private Set<PeerSocket> peerSocketsValidated;
    private Set<PeerSocket> socketsNeedValidating;

    private FloatillaConfig config;

    public Floatilla(FloatillaConfig config) {
        this.config = config;
        this.socketsNeedValidating = config.getSeeds();
        this.peerSocketsValidated = new HashSet<>();
    }

    public void validateSockets(){
        if(!peerSocketsValidated.isEmpty()){
            this.socketsNeedValidating.addAll(this.peerSocketsValidated);
        }
        Set<PeerSocket> currentlyValidating = this.socketsNeedValidating;

    }

    public void testSocket(PeerSocket socket){
        System.out.println("Testing socket: "+socket.toString());
        try {
            if (!config.useSecureConnections()) {
                URL url = new URL("http://" + socket.getHostname() + ":" + Integer.toString(socket.getPort()));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoOutput(true);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes("/floatillaTestChannel?host=" + config.getMyHostname() + "&port=" + Integer.toString(config.getListeningPort()));
                out.flush();
                out.close();
            } else {
                //use https
            }

        } catch (SocketTimeoutException e){
            //handle this timeout by removing this socket from the Set
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
