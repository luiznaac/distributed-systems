/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.jupiter.api.Test;
import com.mycompany.digitalsignature.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Caio
 */
public class DigitalSignatureTest {
    
    public DigitalSignatureTest() {
    }

    @Test
    public void testArea() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //Remetente Gera Assinatura Digital para uma Mensagem
        SenderDigitalSignature remetenteAssiDig = new SenderDigitalSignature();
        String mensagem = "Exemplo de mensagem.";
        byte[] assinatura = remetenteAssiDig.geraAssinatura(mensagem);
        //Guarda Chave Pública para ser Enviada ao Destinatário
        PublicKey pubKey = remetenteAssiDig.getPubKey();

        //Destinatário recebe dados correto
        ReceiverDigitalSignature destinatarioAssiDig = new ReceiverDigitalSignature();
        assertEquals(destinatarioAssiDig.recebeMensagem(pubKey, mensagem, assinatura), true);

        //Destinatário recebe mensagem alterada
        String msgAlterada = "Exemplo de mensagem alterada.";
        assertEquals(destinatarioAssiDig.recebeMensagem(pubKey, msgAlterada, assinatura), false);

        //Criando outra Assinatura
        String mensagem2 = "Exemplo de outra mensagem.";
        byte[] assinatura2 = remetenteAssiDig.geraAssinatura(mensagem2);
        //Guarda Chave Pública para ser Enviada ao Destinatário
        PublicKey pubKey2 = remetenteAssiDig.getPubKey();

        //Destinatário recebe outra assinatura
        assertEquals(destinatarioAssiDig.recebeMensagem(pubKey, mensagem, assinatura2), false);

        //Destinatário recebe outra chave pública
        assertEquals(destinatarioAssiDig.recebeMensagem(pubKey2, mensagem, assinatura), false);
    }

}
