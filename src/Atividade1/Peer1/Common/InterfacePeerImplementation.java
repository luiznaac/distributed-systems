package Common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterfacePeerImplementation extends UnicastRemoteObject implements InterfacePeer{
    
    private String peerName;
    private LinkedHashMap<String, InterfacePeer> lineAccount1;
    private LinkedHashMap<String, InterfacePeer> lineAccount2;
    private LinkedHashMap<InterfacePeer, PublicKey> peerPublicKeys;
    private AccountState account1State;
    private AccountState account2State;
    
    public InterfacePeerImplementation() throws RemoteException {}
    
    public InterfacePeerImplementation(String peerName) throws RemoteException {
        this.peerName = peerName;
        this.lineAccount1 = new LinkedHashMap<>();
        this.lineAccount2 = new LinkedHashMap<>();
        this.peerPublicKeys  = new LinkedHashMap<>();
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
    public void registerPublicKey(InterfacePeer peerReference, PublicKey publicKey) throws RemoteException {
        peerPublicKeys.put(peerReference, publicKey);
    }

    @Override
    public boolean registerPeerAccount1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerReference), digitalSignature)) {
                return false;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (this.account1State == AccountState.RELEASED) {
            return true;
        }
        lineAccount1.put(peerReference.getPeerName(), peerReference);
        return false;
    }
    
    @Override
    public void answerPeerAccount1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerReference), digitalSignature)) {
                return;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        Peer.numberOfAnswers++;
    }
    
    @Override
    public void setAccount1State(AccountState state) throws RemoteException {
        this.account1State = state;
    }
    
    @Override
    public void releaseFirstPeerFromLine1(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException {
        for (InterfacePeer peer : this.lineAccount1.values()) {
            peer.answerPeerAccount1(peerReference, message, digitalSignature);
        }
    }
         
    @Override
    public boolean registerPeerAccount2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerReference), digitalSignature)) {
                return false;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (this.account2State == AccountState.RELEASED) {
            return true;
        }
        lineAccount2.put(peerReference.getPeerName(), peerReference);
        return false;
    }
    
    @Override
    public void answerPeerAccount2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerReference), digitalSignature)) {
                return;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        Peer.numberOfAnswers++;
    }
    
    @Override
    public void releaseFirstPeerFromLine2(InterfacePeer peerReference, String message, byte[] digitalSignature) throws RemoteException {
        for (InterfacePeer peer : this.lineAccount2.values()) {
            peer.answerPeerAccount1(peerReference, message, digitalSignature);
        }
    }
    
    @Override
    public void setAccount2State(AccountState state) throws RemoteException {
        this.account2State = state;
    }
    
    public boolean verifyMessage(String message, PublicKey public_key, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature client_signature = Signature.getInstance("DSA");
        client_signature.initVerify(public_key);
        client_signature.update(message.getBytes());

        if (client_signature.verify(signature)) {
//            System.out.println("A Mensagem recebida foi assinada corretamente.");
            return true;
        }

//        System.out.println("A Mensagem recebida NÃO pode ser validada.");
        return false;
    }
}