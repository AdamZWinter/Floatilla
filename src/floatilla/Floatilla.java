package floatilla;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        for (PeerSocket socket: currentlyValidating
             ) {
            testSocket(socket);
        }
    }

    public void testSocket(PeerSocket socket){
        System.out.println("Testing socket: "+socket.toString());
        BufferedReader br = null;
        try {
            if (!config.useSecureConnections()) {
                URL url = new URL("http://" + socket.getHostname() + ":" + socket.getPort());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoOutput(true);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes("/floatillaTestChannel?host=" + config.getMyHostname() + "&port=" + config.getListeningPort());
                out.flush();
                out.close();

                if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null) {
                        System.out.println(strCurrentLine);
                    }
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null) {
                        System.out.println(strCurrentLine);
                    }
                }
            } else {
                //use https
            }

        } catch (SocketTimeoutException e){
            System.out.println("Connection timed out.");
            //handle this timeout by removing this socket from the Set
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
