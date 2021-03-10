package DigitalSignature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;


public class ReceiverDigitalSignature {

   public boolean recebeMensagem(PublicKey pubKey, String mensagem, byte[] assinatura) throws
   NoSuchAlgorithmException, InvalidKeyException, SignatureException {
       Signature clientSig = Signature.getInstance("DSA");
       clientSig.initVerify(pubKey);
       clientSig.update(mensagem.getBytes());

       if (clientSig.verify(assinatura)) {
           //Mensagem corretamente assinada
          System.out.println("A Mensagem recebida foi assinada corretamente.");
          return true;
       } else {
           //Mensagem não pode ser validada
          System.out.println("A Mensagem recebida NÃO pode ser validada.");
          return false;
       }
   }

}
