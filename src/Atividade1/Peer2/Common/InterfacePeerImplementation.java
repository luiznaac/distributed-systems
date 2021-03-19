package Common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InterfacePeerImplementation extends UnicastRemoteObject implements InterfacePeer{
    
    private String peerName;
    private LinkedHashMap<String, InterfacePeer> lineAccount1;
    private LinkedHashMap<String, InterfacePeer> lineAccount2;
    private int numberOfAnswers;
    private AccountState account1State;
    
    public InterfacePeerImplementation() throws RemoteException {}
    
    public InterfacePeerImplementation(String peerName) throws RemoteException {
        this.peerName = peerName;
        this.lineAccount1 = new LinkedHashMap<>();
        this.lineAccount2 = new LinkedHashMap<>();
        this.numberOfAnswers = 0;
        this.account1State = AccountState.RELEASED;
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
    public void registerPeerAccount1(InterfacePeer peerReference) throws RemoteException {
        if (this.account1State == AccountState.HELD || this.account1State == AccountState.WANTED ) {
            lineAccount1.put(peerReference.getPeerName(), peerReference);
        } else {
            peerReference.answerPeerAccount1();
        }
    }
    
    @Override
    public void answerPeerAccount1() throws RemoteException {
        numberOfAnswers++;
        if (numberOfAnswers == 1) {
            this.account1State = AccountState.HELD;
            accessAccount1();
            Peer.gotAllAnswers = true;
            numberOfAnswers = 0;
            this.account1State = AccountState.RELEASED;
            releaseFirstPeerFromLine1();
        }
    }
    
    @Override
    public LinkedHashMap<String, InterfacePeer> getLineAccount1() throws RemoteException {
        return this.lineAccount1;
    }
    
    @Override
    public void registerPeerAccount2(InterfacePeer peerReference) throws RemoteException {
        lineAccount2.put(peerReference.getPeerName(), peerReference);
    }
    
    @Override
    public LinkedHashMap<String, InterfacePeer> getLineAccount2() throws RemoteException {
        return this.lineAccount2;
    }
    
    public void accessAccount1() {
        Account1.getInstance().insertMoney(5);
        System.out.println("Saldo: " + Account1.getInstance().getBalance());
    }
    
    public void setAccount1State(AccountState state) throws RemoteException {
        this.account1State = state;
    }
    
    private void releaseFirstPeerFromLine1() throws RemoteException {
        for (InterfacePeer peer : this.lineAccount1.values()) {
            peer.answerPeerAccount1();
        }
    }
}
