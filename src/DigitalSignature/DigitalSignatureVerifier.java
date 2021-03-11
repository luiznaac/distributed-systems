package DigitalSignature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;


public class DigitalSignatureVerifier {

    public boolean verifyMessage(String message, PublicKey public_key, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature client_signature = Signature.getInstance("DSA");
        client_signature.initVerify(public_key);
        client_signature.update(message.getBytes());

        if (client_signature.verify(signature)) {
            System.out.println("A Mensagem recebida foi assinada corretamente.");
            return true;
        }

        System.out.println("A Mensagem recebida NÃO pode ser validada.");
        return false;
    }

}
