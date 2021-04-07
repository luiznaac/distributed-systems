package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface InterfacePeer extends Remote{
    String getPeerName() throws RemoteException;
    boolean registerPeerAccount1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    void answerPeerAccount1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    void setAccount1State(AccountState state) throws RemoteException;
    void releaseFirstPeerFromLine1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    boolean registerPeerAccount2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    void answerPeerAccount2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    void setAccount2State(AccountState state) throws RemoteException;
    void releaseFirstPeerFromLine2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException;
    void registerPublicKey(InterfacePeer peerReference, PublicKey publicKey) throws RemoteException;
}
