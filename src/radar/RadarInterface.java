package radar;

import pozicijePack.Pozicija;
import pozicijePack.PozicijeLista;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RadarInterface extends Remote {
    PozicijeLista dohvatiSvePozicije(Pozicija pozicija) throws RemoteException;
    Boolean vracanjeUKomandniCentar(Pozicija pozicija) throws RemoteException;
    void vracen(Pozicija p)throws RemoteException;
}
