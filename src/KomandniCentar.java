import com.esotericsoftware.kryo.Kryo;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import pozicijePack.Pozicija;
import pozicijePack.PozicijeLista;
import pozicijePack.Tip;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;



public class KomandniCentar {
    private ConcurrentHashMap<String,Pozicija> mapa = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Connection> mapaKomande = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Long> mapaVremena = new ConcurrentHashMap<>();
    private Server server;
    private String slovo;
    private int port;


    public KomandniCentar(String slovo, int port) throws IOException {
        server = new Server(50000,50000);
        this.slovo = slovo;
        this.port = port;
        Kryo kryo = server.getKryo();
        kryo.register(Pozicija.class);
        kryo.register(Tip.class);
        kryo.register(Avion.class);
        kryo.register(String.class);
        kryo.register(ArrayList.class);
        kryo.register(PozicijeLista.class);
        server.addListener(new Listener(){

            public void connected(Connection connection) {
                System.out.println("Novi avion se priključio sistemu: " + connection.getID());
            }
            public void received(Connection connection, Object object) {

                if (object instanceof PozicijeLista) {
                    PozicijeLista pristiglaLista = (PozicijeLista) object;
                    synchronized (mapa) {
                        for (Pozicija p : pristiglaLista.getListaPozicija()) {
                            mapa.put(p.getImeAviona(), p);
                            mapaKomande.put(p.getImeAviona(), connection);
                            mapaVremena.put(p.getImeAviona(), System.currentTimeMillis());

                        }
                    }
                }
                if(object instanceof String){
                    String poruka = (String) object;
                    if(poruka.startsWith("STIGAO")){
                        String brisanjeAviona = poruka.split(":")[1];
                        synchronized (mapa){
                            mapa.remove(brisanjeAviona);
                            mapaKomande.remove(brisanjeAviona);

                        }

                    }
                }


            }
            public void disconnected(Connection connection) {
                System.out.println("Avion: " + connection.getID() + " je napustio kontrolu leta.");
            }

        });
        server.start();
        server.bind(port);
        System.out.println("Pokrenut komandni centar");
    }
    public void vracanje(String ime){
    Connection c = mapaKomande.get(ime);
    if(c != null){
        c.sendTCP("NAZAD:"+ime);
        System.out.println("Poslata komanda za povratak avionu: " + ime);

    }
    else{
        System.out.println("Greska vracanja");
    }
    }
    public void ispis() {
        long trenutno = System.currentTimeMillis();
        String crvena = "\u001B[31m";
        String plava = "\u001B[34m";
        String reset = "\u001B[0m";
        synchronized (mapa) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    boolean prazno = true;
                    for (Pozicija p : mapa.values()) {
                        Long vidjen = mapaVremena.get(p.getImeAviona());
                        if (vidjen != null && (trenutno - vidjen) < 3000) {
                            if (p.getX() > i && p.getX() < i + 1 && p.getY() > j && p.getY() < j + 1) {
                                String boja = p.getImeAviona().startsWith("P") ? plava : crvena;
                                String tekst = p.getImeAviona() + "(" + p.getTip().name() + ")";
                                System.out.printf("%s%-12s%s", boja, tekst, reset);
                                prazno = false;

                            }

                        }
                    }
                    if (prazno) {
                        System.out.printf("%-12s", "*");
                    }

                }
                System.out.println("\n");
            }
        }
    }




}
class crveniStart{
    public static void main(String[] args) throws IOException {
        KomandniCentar kc = new KomandniCentar("C", 20000);
        Scanner sc = new Scanner(System.in);
        while (true) {
            if (sc.hasNextLine()) {
                String komanda = sc.nextLine();
               if (komanda.equals("1")) {
                    kc.ispis();
                }
               else if(komanda.equals("2")){
                   System.out.println("Ukucati ime aviona:");
                   if(sc.hasNextLine()) {
                       String ime = sc.nextLine();
                       kc.vracanje(ime);
                   }
               }
            }

        }
    }
}
class plaviStart{
    public static void main(String[] args) throws IOException {
        KomandniCentar kc = new KomandniCentar("P",10000);
        Scanner sc = new Scanner(System.in);
        while (true) {
            if (sc.hasNextLine()) {
                String komanda = sc.nextLine();
                if (komanda.equals("1")) {
                    kc.ispis();
                }
              else if(komanda.equals("2")){
                    System.out.println("Ukucati ime aviona:");
                    if(sc.hasNextLine()) {
                        String ime = sc.nextLine();
                        kc.vracanje(ime);
                    }
                }
            }

        }
    }
}

