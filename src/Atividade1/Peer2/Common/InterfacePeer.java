package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface InterfacePeer extends Remote{
    public abstract String getPeerName() throws RemoteException;
    public abstract void notifyPeer(String notification) throws RemoteException;
    public abstract boolean registerPeerAccount1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    public abstract void answerPeerAccount1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    public void setAccount1State(AccountState state) throws RemoteException;
    public void releaseFirstPeerFromLine1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    public abstract boolean registerPeerAccount2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    public abstract void answerPeerAccount2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    public void setAccount2State(AccountState state) throws RemoteException;
    public void releaseFirstPeerFromLine2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    public void registerPublicKey(InterfacePeer peerReference, PublicKey publicKey) throws RemoteException;
}
