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
    private Set<PeerSocket> blackList;      //but yeah, it's a Set
    private FloatillaConfig config;
    //private boolean hasNew;         //will be made true when new peers are identified that need validating //no just a method that checks things

    public Floatilla(FloatillaConfig config) {
        this.config = config;
        this.socketsNeedValidating = config.getSeeds();
        this.peerSocketsValidated = new HashSet<>();
        this.blackList = new HashSet<>();
    }

    public void stageReValidation(){
        if(!peerSocketsValidated.isEmpty()){
            this.socketsNeedValidating.addAll(this.peerSocketsValidated);
            peerSocketsValidated.clear();
        }
    }

    public void makeValidatingNewOnly(){
        Iterator<PeerSocket> itr = getValidatingIterator();
        while(itr.hasNext()){
            if(peerSocketsValidated.contains(itr.next())){
                itr.remove();
            }
        }
    }

    public FloatillaConfig getConfig(){
        return config;
    }

    public Iterator<PeerSocket> getValidatingIterator(){
        return socketsNeedValidating.iterator();
    }

    public Iterator<PeerSocket> getValidatedIterator(){
        return peerSocketsValidated.iterator();
    }

//    public void fakeValidateAll(){
//        this.peerSocketsValidated.addAll(this.socketsNeedValidating);
//    }

    public void queueForValidation(PeerSocket socket){
        socketsNeedValidating.add(socket);
    }

    public void addValidatedSocket(PeerSocket socket){
        peerSocketsValidated.add(socket);
    }

    //for thread safety while validating
    public Set<PeerSocket> deepCopyValidationSet(){
        Set<PeerSocket> deepishCopy = new HashSet<>();
        Iterator<PeerSocket> itr = socketsNeedValidating.iterator();
        while(itr.hasNext()){
            deepishCopy.add(itr.next());
        }
        return deepishCopy;
    }

    public void clearValidationSet(){
        socketsNeedValidating.clear();
    }

    public Set<PeerSocket> getValidationSet(){return socketsNeedValidating;}

    public void removeSeeds(){
        Set<PeerSocket> seeds = config.getSeeds();
        for(PeerSocket seed : seeds){
            if(peerSocketsValidated.contains(seed)){
                peerSocketsValidated.remove(seed);
            }
            if(socketsNeedValidating.contains(seed)){
                socketsNeedValidating.remove(seed);
            }
        }
    }

    public boolean hasEmptyValidationSet(){
        return socketsNeedValidating.isEmpty();
    }

    public boolean contains(PeerSocket socket){
        return peerSocketsValidated.contains(socket);
    }

    public boolean isBlackListed(PeerSocket socket){
        return blackList.contains(socket);
    }
}
