package DigitalSignature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;


public class DigitalSignature {

	public static void main(String args[]) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		System.out.println("Enter message:");
		Scanner scanner = new Scanner(System.in);
		String message = scanner.nextLine();
		MyKeyPair my_key_pair = (new KeyPairBuilder()).build();

		byte[] signature = (new DigitalSignatureSigner()).signMessage(message, my_key_pair.private_key);

		(new DigitalSignatureVerifier()).verifyMessage(message, my_key_pair.public_key, signature);
	}
}
