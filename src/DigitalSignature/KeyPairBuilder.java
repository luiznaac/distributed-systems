package DigitalSignature;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class KeyPairBuilder {

    public MyKeyPair build() throws NoSuchAlgorithmException {
        KeyPair key_pair = generateKeyPair();

        MyKeyPair my_key_pair = new MyKeyPair();
        my_key_pair.public_key = key_pair.getPublic();
        my_key_pair.private_key = key_pair.getPrivate();

        return my_key_pair;
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
        SecureRandom secure_random = new SecureRandom();
        generator.initialize(512, secure_random);
        return generator.generateKeyPair();
    }
}
