package Common;

import DigitalSignature.KeyPairBuilder;
import DigitalSignature.MyKeyPair;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Peer {
    
    public Peer(String name) throws NoSuchAlgorithmException {
        registeredPeers = new HashMap<>();
        numberOfAnswers = 0;
        numberOfAnswersToAccessResource = 0;
        myKeyPair = (new KeyPairBuilder()).build();
        this.name = name;
    }
        
    private InterfacePeer localPeerReference;
    private Registry namesServiceReference;
    private HashMap<String, InterfacePeer> registeredPeers;
    public static boolean gotAllAnswers;
    public static int numberOfAnswers;
    public static int numberOfAnswersToAccessResource;
    private MyKeyPair myKeyPair;
    private String name;

    private void createRegistry() throws RemoteException {
        //Tentando criar registro em localHost e porta 1099 (Padrão)
        //Se lançar a exceção é porque já existe esse registro. Então pega o bicho
        try {
            this.namesServiceReference = LocateRegistry.createRegistry(1099);
        } catch(ExportException e) {
            this.namesServiceReference = LocateRegistry.getRegistry(1099);
        }
    }
        
    private void initializeLocalReference() throws RemoteException {
        //Inicializa interface do próprio Peer, para registrar no serviço de nomes
        this.localPeerReference = new InterfacePeerImplementation(this.name);
        this.namesServiceReference.rebind(this.name, this.localPeerReference);
    }
    
    private void addRegisteredPeers() throws RemoteException {
        //Busca por todos os peers registrados no serviço de nomes
        //Se não for o próprio tenta colocar no hash
        String[] listOfRegisteredPeers = this.namesServiceReference.list();
        numberOfAnswersToAccessResource = listOfRegisteredPeers.length;
        for (String registeredPeer : listOfRegisteredPeers) {
            if (!registeredPeer.equals(this.name)) {
                try {
                    InterfacePeer intefaceToAdd = (InterfacePeer) this.namesServiceReference.lookup(registeredPeer);
                    this.registeredPeers.put(registeredPeer, intefaceToAdd);
                    intefaceToAdd.registerPublicKey(this.name, myKeyPair.public_key);
                } catch (NotBoundException e) {
                    System.out.println(e);
                }
            }
        }
    }

    private void askForAccessAccount1() throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.addRegisteredPeers();
        numberOfAnswers = 0;
        Account1.getInstance().setState(AccountState.WANTED);
        // Ask for every peer if resource is available.
        // If it is available, increment number of answers, otherwise register access intention
        for (InterfacePeer peer : registeredPeers.values()) {
            String message = "Aqui é o peer3. Quero acessar recurso 1";
            if (peer.registerPeerAccount1(this.name, message, signMessage(message, this.myKeyPair.private_key))) {
                numberOfAnswers++;
            }
        }
    }
    
    private void accessAccount1() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, RemoteException {
        Account1.getInstance().setState(AccountState.HELD);
        System.out.println("Acesso à conta 1 liberado! Deseja depositar (1) ou sacar (2) ?");
        Scanner scan = new Scanner(System.in);
        switch (scan.nextInt()) {
            case 1:
                System.out.println("Conta 1: Quanto deseja depositar?");
                Account1.getInstance().insertMoney(scan.nextInt());
                break;
            case 2:
                System.out.println("Conta 1: Quanto deseja sacar?");
                Account1.getInstance().withdrawMoney(scan.nextInt());
                break;
            default:
                System.out.println("Opção inválida");
        }

        System.out.println("Conta 1 - Saldo: " + Account1.getInstance().getBalance());
        gotAllAnswers = true;
        Account1.getInstance().setState(AccountState.RELEASED);
        String message = "Aqui é o peer3. Recurso 1 liberado";
        releasePeersFromLine1(
            message,
            signMessage(message, this.myKeyPair.private_key)
        );
        numberOfAnswers = 0;
    }

    private void releasePeersFromLine1(String message, byte[] digitalSignature) throws RemoteException {
        ArrayList<String> line = Account1.getInstance().line;
        for (String peerName : line) {
            InterfacePeer peerReference = this.registeredPeers.get(peerName);
            peerReference.answerPeerAccount1(this.name, message, digitalSignature);
        }
        line.clear();
    }
    
    private void askForAccessAccount2() throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.addRegisteredPeers();
        numberOfAnswers = 0;
        Account2.getInstance().setState(AccountState.WANTED);
        for (String peerName : registeredPeers.keySet()) {
            if (!peerName.equals(this.name)) {
                String message = "Aqui é o peer3. Quero acessar recurso 2";
                InterfacePeer peerReference = registeredPeers.get(peerName);
                if (peerReference.registerPeerAccount2(this.name, message, signMessage(message, this.myKeyPair.private_key))) {
                    numberOfAnswers++;
                }
            }
        }
    }
    
    private void accessAccount2() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, RemoteException {
        Account2.getInstance().setState(AccountState.HELD);
        System.out.println("Acesso à conta 2 liberado! Deseja depositar (1) ou sacar (2) ?");
        Scanner scan = new Scanner(System.in);
        switch (scan.nextInt()) {
            case 1:
                System.out.println("Conta 2: Quanto deseja depositar?");
                Account2.getInstance().insertMoney(scan.nextInt());
                break;
            case 2:
                System.out.println("Conta 2: Quanto deseja sacar?");
                Account2.getInstance().withdrawMoney(scan.nextInt());
                break;
            default:
                System.out.println("Opção inválida");
        }

        System.out.println("Conta 2 - Saldo: " + Account2.getInstance().getBalance());
        gotAllAnswers = true;
        Account2.getInstance().setState(AccountState.RELEASED);
                String message = "Aqui é o peer3. Recurso 2 liberado";
        releasePeersFromLine2(
            message,
            signMessage(message, this.myKeyPair.private_key)
        );
        numberOfAnswers = 0;
    }

    private void releasePeersFromLine2(String message, byte[] digitalSignature) throws RemoteException {
        ArrayList<String> line = Account2.getInstance().line;
        for (String peerName : line) {
            InterfacePeer peerReference = this.registeredPeers.get(peerName);
            peerReference.answerPeerAccount2(this.name, message, digitalSignature);
        }
        line.clear();
    }

    private byte[] signMessage(String message, PrivateKey private_key)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("DSA");

        signature.initSign(private_key);
        signature.update(message.getBytes());
        return signature.sign();
    }
    
    public static void main(String[] args) throws RemoteException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        Peer peer = new Peer("Peer3");
        peer.createRegistry();
        peer.initializeLocalReference();
        peer.addRegisteredPeers();
        
        gotAllAnswers = true;
        
        while(true) {
            if (gotAllAnswers) {
                gotAllAnswers = false;
                Scanner scan = new Scanner(System.in);
                System.out.println("Qual recurso desejas acessar?");
                switch (scan.nextInt()) {
                    case 1:
                        peer.askForAccessAccount1();
                        while(numberOfAnswers < numberOfAnswersToAccessResource - 1) {
                            sleep(200);
                        }
                        peer.accessAccount1();
                        break;
                    case 2:
                        peer.askForAccessAccount2();
                        while(numberOfAnswers < numberOfAnswersToAccessResource - 1) {
                            sleep(200);
                        }
                        peer.accessAccount2();
                        break;
                    default:
                        System.out.println("Opção inválida");
                }
            }
        }
    }
}
