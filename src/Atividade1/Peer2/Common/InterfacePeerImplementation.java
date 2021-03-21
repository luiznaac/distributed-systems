package Common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;

public class InterfacePeerImplementation extends UnicastRemoteObject implements InterfacePeer{
    
    private String peerName;
    private LinkedHashMap<String, InterfacePeer> lineAccount1;
    private LinkedHashMap<String, InterfacePeer> lineAccount2;
    private AccountState account1State;
    private AccountState account2State;
    
    public InterfacePeerImplementation() throws RemoteException {}
    
    public InterfacePeerImplementation(String peerName) throws RemoteException {
        this.peerName = peerName;
        this.lineAccount1 = new LinkedHashMap<>();
        this.lineAccount2 = new LinkedHashMap<>();
        this.account1State = AccountState.RELEASED;
        this.account2State = AccountState.RELEASED;
    }
    
    @Override
    public String getPeerName() throws RemoteException {
        return this.peerName;
    }
        
    @Override
    public void notifyPeer(String notification) throws RemoteException {
        System.out.println("Olá. Este é o peer " + this.peerName + " e a mensagem é: " + notification);
    }

    @Override
    public boolean registerPeerAccount1(InterfacePeer peerReference) throws RemoteException {
        if (this.account1State == AccountState.RELEASED) {
            return true;
        }
        lineAccount1.put(peerReference.getPeerName(), peerReference);
        return false;
    }
    
    @Override
    public void answerPeerAccount1() throws RemoteException {
        Peer.numberOfAnswers++;
    }
    
    @Override
    public void setAccount1State(AccountState state) throws RemoteException {
        this.account1State = state;
    }
    
    @Override
    public void releaseFirstPeerFromLine1() throws RemoteException {
        for (InterfacePeer peer : this.lineAccount1.values()) {
            peer.answerPeerAccount1();
        }
        this.lineAccount1.clear();
    }
         
    @Override
    public boolean registerPeerAccount2(InterfacePeer peerReference) throws RemoteException {
        if (this.account2State == AccountState.RELEASED) {
            return true;
        }
        lineAccount2.put(peerReference.getPeerName(), peerReference);
        return false;
    }
    
    @Override
    public void answerPeerAccount2() throws RemoteException {
        Peer.numberOfAnswers++;
    }
    
    @Override
    public void releaseFirstPeerFromLine2() throws RemoteException {
        for (InterfacePeer peer : this.lineAccount2.values()) {
            peer.answerPeerAccount2();
        }
        this.lineAccount2.clear();
    }
    
    @Override
    public void setAccount2State(AccountState state) throws RemoteException {
        this.account2State = state;
    }
}