package radar;

import pozicijePack.Pozicija;
import pozicijePack.PozicijeLista;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Radar implements RadarInterface{
    ConcurrentHashMap<String,Pozicija> svePozicije = new ConcurrentHashMap<>();

    @Override
    public PozicijeLista dohvatiSvePozicije(Pozicija pozicija) throws RemoteException {
        ArrayList<Pozicija> pozicijeURadaru = new ArrayList<>();
        svePozicije.put(pozicija.getImeAviona(),pozicija);
        for(Pozicija p: svePozicije.values()){
            double udaljenost = pozicija.euklidskoRastojanje(p);
            if(udaljenost < pozicija.getTip().getDometRadara()){
                pozicijeURadaru.add(p);
            }
        }
        return new PozicijeLista(pozicijeURadaru);
    }

    @Override
    public Boolean vracanjeUKomandniCentar(Pozicija pozicija) throws RemoteException {
        if(svePozicije.containsKey(pozicija.getImeAviona())){
            svePozicije.remove(pozicija.getImeAviona());
            return true;
        }
        return false;
    }

    @Override
    public void vracen(Pozicija p) throws RemoteException {
        svePozicije.remove(p.getImeAviona());

            synchronized (System.out) {
                System.out.println("Vracen " + p.getImeAviona());
            }
        System.out.println("Ostatak aviona: " + svePozicije.keySet());

    }

    public static void main(String[] args) throws RemoteException {
        Radar radar = new Radar();
        RadarInterface stub = (RadarInterface) UnicastRemoteObject.exportObject(radar, 0);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("radar", stub);


    }
}
