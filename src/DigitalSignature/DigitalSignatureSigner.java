package DigitalSignature;

import java.security.*;


public class DigitalSignatureSigner {

    public byte[] signMessage(String message, PrivateKey private_key)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("DSA");

        signature.initSign(private_key);
        signature.update(message.getBytes());
        return signature.sign();
    }

}
