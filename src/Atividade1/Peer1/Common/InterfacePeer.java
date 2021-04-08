package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface InterfacePeer extends Remote{
    boolean registerPeerAccount1(String peerName, String message, byte[] digitalSignature) throws RemoteException;
    void answerPeerAccount1(String peerName, String message, byte[] digitalSignature) throws RemoteException;
    boolean registerPeerAccount2(String peerName, String message, byte[] digitalSignature) throws RemoteException;
    void answerPeerAccount2(String peerName, String message, byte[] digitalSignature) throws RemoteException;
    void registerPublicKey(String peerName, PublicKey publicKey) throws RemoteException;
}
