package Common;

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
    }
        
    private InterfacePeer localPeerReference;
    private Registry namesServiceReference;
    private HashMap<String, InterfacePeer> registeredPeers;
    public static boolean gotAllAnswers;
    
    
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
        this.localPeerReference = new InterfacePeerImplementation("Peer1");
        this.namesServiceReference.rebind(this.localPeerReference.getPeerName(), this.localPeerReference);
    }
    
    public void addRegisteredPeers() throws RemoteException {
        //Busca por todos os peers registrados no serviço de nomes
        //Se não for o próprio tenta colocar no hash
        String[] listOfRegisteredPeers = this.namesServiceReference.list();
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
        this.localPeerReference.setAccount1State(AccountState.WANTED);
        for (InterfacePeer peer : registeredPeers.values()) {
            peer.registerPeerAccount1(this.localPeerReference);
        }
    }

    
    public static void main(String[] args) throws RemoteException, NotBoundException{
        Peer peer = new Peer();
        peer.createRegistry();
        peer.initilizeLocalReference();
        peer.addRegisteredPeers();
        
        gotAllAnswers = true;
        
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Esperando outro peer.");
        while(true) {
            if (gotAllAnswers) {
                gotAllAnswers = false;
                System.out.println("Qual recurso desejas acessar?");
                scan.nextLine();
                peer.askForAccessAcount1();
            }
        }
        
    }
}
