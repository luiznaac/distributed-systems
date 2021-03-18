package Common;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

public class Peer {
    public static void main(String[] args) throws RemoteException, NotBoundException{
//        //Pegando registro em localHost e porta 1099 (Padrão)
//        Registry namesServiceReference = LocateRegistry.getRegistry(1099);
//        
//        //Verifica de registro é nulo. Se sim, cria um novo registro
//        if (namesServiceReference == null) {
//            namesServiceReference = LocateRegistry.createRegistry(1099);
//        }
        Registry namesServiceReference;
        
        try {
            namesServiceReference = LocateRegistry.createRegistry(1099);
        } catch(ExportException e) {
            namesServiceReference = LocateRegistry.getRegistry(1099);
        }
        //Inicializa interface do próprio Peer, para registrar no serviço de nomes
        InterfacePeer localPeerReference = new InterfacePeerImplementation("Peer1");
        namesServiceReference.rebind(localPeerReference.getPeerName(), localPeerReference);
        
        //Pegando referência da interface remota de Peer1
        List<InterfacePeer> listOfPeers = new ArrayList<>();
        try {
            listOfPeers.add((InterfacePeer) namesServiceReference.lookup("Peer2"));        
            listOfPeers.get(0).notifyPeer("Oi, peer2. Aqui é o peer1."
                    + "Preciso arrumar esta lista de peers. Adeus");
        } catch(NotBoundException e) {
            System.out.println(e);
        }
        
        System.out.println("Esperando outro peer.");
        while(true) {}
    }
}
