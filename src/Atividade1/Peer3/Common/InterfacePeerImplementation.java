package Common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterfacePeerImplementation extends UnicastRemoteObject implements InterfacePeer{
    
    public String peerName;
    private LinkedHashMap<String, PublicKey> peerPublicKeys;

    public InterfacePeerImplementation(String peerName) throws RemoteException {
        this.peerName = peerName;
        this.peerPublicKeys = new LinkedHashMap<>();
    }
    
    @Override
    public void registerPublicKey(String peerName, PublicKey publicKey) throws RemoteException {
        peerPublicKeys.put(peerName, publicKey);
    }

    @Override
    public boolean registerPeerAccount1(String peerName, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerName), digitalSignature)) {
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (Account1.getInstance().state == AccountState.RELEASED) {
            return true;
        }
        Account1.getInstance().line.add(peerName);
        return false;
    }

    @Override
    public void answerPeerAccount1(String peerName, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerName), digitalSignature)) {
                return;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        Peer.numberOfAnswers++;
    }

    @Override
    public boolean registerPeerAccount2(String peerName, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerName), digitalSignature)) {
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (Account2.getInstance().state == AccountState.RELEASED) {
            return true;
        }
        Account2.getInstance().line.add(peerName);
        return false;
    }

    @Override
    public void answerPeerAccount2(String peerName, String message, byte[] digitalSignature) throws RemoteException {
        try {
            if (!verifyMessage(message, peerPublicKeys.get(peerName), digitalSignature)) {
                return;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(InterfacePeerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        Peer.numberOfAnswers++;
    }
    
    private boolean verifyMessage(String message, PublicKey public_key, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature client_signature = Signature.getInstance("DSA");
        client_signature.initVerify(public_key);
        client_signature.update(message.getBytes());

        return client_signature.verify(signature);
    }
}
