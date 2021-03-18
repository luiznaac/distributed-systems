package Common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class InterfacePeerImplementation extends UnicastRemoteObject implements InterfacePeer{
    
    private String peerName;
    private List<InterfacePeer> listOfPeers;
    
    public InterfacePeerImplementation() throws RemoteException {}
    
    public InterfacePeerImplementation(String peerName) throws RemoteException {
        this.peerName = peerName;
    }
    
    @Override
    public String getPeerName() throws RemoteException {
        return this.peerName;
    }

    @Override
    public void registerPeer(InterfacePeer peerReference) throws RemoteException {
        listOfPeers.add(peerReference);
    }
    
    @Override
    public void notifyPeer(String notification) throws RemoteException {
        System.out.println("Olá. Este é o peer " + this.peerName + " e a mensagem é: " + notification);
    }
    
    @Override
    public List<InterfacePeer> getListOfPeers() throws RemoteException {
        return this.listOfPeers;
    }
}
