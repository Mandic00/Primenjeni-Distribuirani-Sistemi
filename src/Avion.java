import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import pozicijePack.Pozicija;
import pozicijePack.PozicijeLista;
import pozicijePack.Tip;
import radar.RadarInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Avion implements Runnable {
    private int id;
    private String imeAviona;
    private Tip tip;
    private Pozicija pozicija;
    private volatile boolean leti = true;
    private RadarInterface stub;
    private double bazaX, bazaY;

    private Client klijent;
    public Pozicija getPozicija() {
        return pozicija;
    }

    public Avion(){

    }
    public void setPozicija(Pozicija pozicija) {
        this.pozicija = pozicija;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Avion(int id, String imeAviona, Tip tip, Pozicija pozicija, boolean leti,Client klijent) {
        this.id = id;
        this.imeAviona = imeAviona;
        this.tip = tip;
        this.pozicija = pozicija;
        this.leti = leti;
        this.klijent = klijent;

        if(imeAviona.startsWith("P")){
            bazaX = 0.5;
            bazaY = 0.5;
        }
        else {
            bazaX = 7.5;
            bazaY = 7.5;
        }
    }

    @Override
    public void run() {
        try{
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.stub = (RadarInterface)registry.lookup("radar");

            klijent.addListener(new Listener(){
                public void received(Connection connection, Object object){

                    if(object instanceof String){
                        String poruka = (String)object;
                        if(poruka.contains(":")){
                            String[] split = poruka.split(":");
                            String komanda = split[0];
                            String ime = split[1];

                            if(komanda.equals("NAZAD") && ime.equals(imeAviona)){
                                leti = false;
                                klijent.removeListener(this);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Avion " + id + "tipa:  " + tip + " je poleteo!");
        double[] koraci = {0.1,0.0,-0.1};
        while(leti) {
            double dx = koraci[(int)(Math.random()*3)];
            double dy = koraci[(int)(Math.random()*3)];
            synchronized (pozicija) {
                double novaPozicijaX = pozicija.getX() + dx;
                double novaPozicijaY = pozicija.getY() + dy;
                if (novaPozicijaX >= 0 && novaPozicijaX <= 8) {
                    pozicija.setX(novaPozicijaX);
                }
                if (novaPozicijaY >= 0 && novaPozicijaY <= 8) {
                    pozicija.setY(novaPozicijaY);
                }
            }
            synchronized (System.out) {
                System.out.printf("Avion %s Model: %s je na poziciji %.1f %.1f\n", id, tip.name(), pozicija.getX(), pozicija.getY());
            }
            try {
                PozicijeLista dohvaceni= stub.dohvatiSvePozicije(pozicija);
                synchronized (klijent) {
                    klijent.sendTCP(dohvaceni);
                }

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            try{
                Thread.sleep(2000/tip.getBrzina());
            }catch(InterruptedException e){
                leti = false;
            }
        }
        vracanje(pozicija,tip.getBrzina(),bazaX,bazaY);
    }

    public void vracanje(Pozicija p, int brzina,double x, double y) {

        System.out.println("Vracanje aviona" + id);
        while(Math.abs(p.getX()-x)>0.1 || Math.abs(p.getY()-y)>0.1){
           if(p.getX() < x){
               p.setX(p.getX() + 0.1);
           }
           else if(p.getX() > x){
               p.setX(p.getX() - 0.1);
           }
            if(p.getY() < y){
                p.setY(p.getY() + 0.1);
            }
            else if(p.getY() > y){
                p.setY(p.getY() - 0.1);
            }


           try {
               stub.dohvatiSvePozicije(p);
               Thread.sleep(1000/brzina);
           }catch (Exception e) {
               p.setX(x);
               p.setY(y);
           }

        }
        try {
            stub.vracen(p);
            synchronized (klijent){
                klijent.sendTCP("STIGAO:"+imeAviona);
            }
            System.out.println("Vratio se " + id);
        }catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}
