package pozicijePack;

import java.io.Serializable;
import java.util.ArrayList;

public class PozicijeLista implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Pozicija> listaPozicija = new ArrayList<>();

    public PozicijeLista(ArrayList<Pozicija> listaPozicija) {
        this.listaPozicija = listaPozicija;
    }
    public ArrayList<Pozicija> getListaPozicija() {
        return listaPozicija;
    }
    public PozicijeLista(){

    }
}