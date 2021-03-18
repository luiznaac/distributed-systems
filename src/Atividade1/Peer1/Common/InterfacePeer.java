package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfacePeer extends Remote{
    public abstract String getPeerName() throws RemoteException;
    public abstract void registerPeer(InterfacePeer peerReference) throws RemoteException;
    public abstract void notifyPeer(String notification) throws RemoteException;
    public abstract List<InterfacePeer> getListOfPeers() throws RemoteException;
}
