package Common;

import static java.lang.Thread.sleep;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Scanner;

public class Peer {
    
    public Peer() {
        registeredPeers = new HashMap<>();
        numberOfAnswers = 0;
        numberOfAnswersToAccessResource = 0;
    }
        
    private InterfacePeer localPeerReference;
    private Registry namesServiceReference;
    private HashMap<String, InterfacePeer> registeredPeers;
    public static boolean gotAllAnswers;
    public static int numberOfAnswers;
    public static int numberOfAnswersToAccessResource;
    
    
    public void createRegistry() throws RemoteException {
        //Tentando criar registro em localHost e porta 1099 (Padrão)
        //Se lançar a exceção é porque já existe esse registro. Então pega o bicho
        try {
            this.namesServiceReference = LocateRegistry.createRegistry(1099);
        } catch(ExportException e) {
            this.namesServiceReference = LocateRegistry.getRegistry(1099);
        }
    }
        
    public void initilizeLocalReference() throws RemoteException {
        //Inicializa interface do próprio Peer, para registrar no serviço de nomes
        this.localPeerReference = new InterfacePeerImplementation("Peer3");
        this.namesServiceReference.rebind(this.localPeerReference.getPeerName(), this.localPeerReference);
    }
    
    public void addRegisteredPeers() throws RemoteException {
        //Busca por todos os peers registrados no serviço de nomes
        //Se não for o próprio tenta colocar no hash
        String[] listOfRegisteredPeers = this.namesServiceReference.list();
        numberOfAnswersToAccessResource = listOfRegisteredPeers.length;
        for (String registeredPeer : listOfRegisteredPeers) {
            if (!registeredPeer.equals(this.localPeerReference.getPeerName())) {
                try {
                    this.registeredPeers.put(registeredPeer, 
                            (InterfacePeer) this.namesServiceReference.lookup(registeredPeer));
                    //pingPeer(this.registeredPeers.get(registeredPeer));
                } catch (NotBoundException e) {
                    System.out.println(e);
                }
            }
        }
    }
    
    public void pingPeer(InterfacePeer peer) throws RemoteException {
        peer.notifyPeer("Oi, peer2. Aqui é o peer1."
                    + "Preciso arrumar esta lista de peers. Adeus");
    }
    
    public void askForAccessAcount1() throws RemoteException {
        this.addRegisteredPeers();
        numberOfAnswers = 0;
        this.localPeerReference.setAccount1State(AccountState.WANTED);
        for (InterfacePeer peer : registeredPeers.values()) {
            if (!peer.getPeerName().equals(this.localPeerReference.getPeerName())) {
                if (peer.registerPeerAccount1(this.localPeerReference)) {
                    numberOfAnswers++;
                }
            }
        }
    }
    
    public void accessAccount1() throws RemoteException {
        this.localPeerReference.setAccount1State(AccountState.HELD);
        System.out.println("Acesso à conta 1 liberado! Deseja depositar (1) ou sacar (2) ?");
        Scanner scan = new Scanner(System.in);
        switch (scan.nextInt()) {
            case 1:
                System.out.println("Quanto deseja depositar?");
                Account1.getInstance().insertMoney(scan.nextInt());
                break;
            case 2:
                System.out.println("Quanto deseja sacar?");
                Account1.getInstance().withdrawMoney(scan.nextInt());
                break;
            default:
                System.out.println("Opção inválida");
        }

        System.out.println("Saldo: " + Account1.getInstance().getBalance());
        gotAllAnswers = true;
        this.localPeerReference.setAccount1State(AccountState.RELEASED);
        this.localPeerReference.releaseFirstPeerFromLine1();
        this.numberOfAnswers = 0;
    }
    
    public void askForAccessAcount2() throws RemoteException {
        this.addRegisteredPeers();
        numberOfAnswers = 0;
        this.localPeerReference.setAccount2State(AccountState.WANTED);
        for (InterfacePeer peer : registeredPeers.values()) {
            if (!peer.getPeerName().equals(this.localPeerReference.getPeerName())) {
                if (peer.registerPeerAccount2(this.localPeerReference)) {
                    numberOfAnswers++;
                }
            }
        }
    }
    
    public void accessAccount2() throws RemoteException {
        this.localPeerReference.setAccount2State(AccountState.HELD);
        System.out.println("Acesso à conta 2 liberado! Deseja depositar (1) ou sacar (2) ?");
        Scanner scan = new Scanner(System.in);
        switch (scan.nextInt()) {
            case 1:
                System.out.println("Quanto deseja depositar?");
                Account2.getInstance().insertMoney(scan.nextInt());
                break;
            case 2:
                System.out.println("Quanto deseja sacar?");
                Account2.getInstance().withdrawMoney(scan.nextInt());
                break;
            default:
                System.out.println("Opção inválida");
        }

        System.out.println("Saldo: " + Account2.getInstance().getBalance());
        gotAllAnswers = true;
        this.localPeerReference.setAccount2State(AccountState.RELEASED);
        this.localPeerReference.releaseFirstPeerFromLine2();
        this.numberOfAnswers = 0;
    }

    
    public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException{
        Peer peer = new Peer();
        peer.createRegistry();
        peer.initilizeLocalReference();
        peer.addRegisteredPeers();
        
        gotAllAnswers = true;
        
        System.out.println("Esperando outro peer.");
        while(true) {
            if (gotAllAnswers) {
                gotAllAnswers = false;
                Scanner scan = new Scanner(System.in);
                System.out.println("Qual recurso desejas acessar?");
                switch (scan.nextInt()) {
                    case 1:
                        peer.askForAccessAcount1();
                        while(numberOfAnswers < numberOfAnswersToAccessResource - 1) {
                            sleep(200);
                        }
                        peer.accessAccount1();
                        break;
                    case 2:
                        peer.askForAccessAcount2();
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