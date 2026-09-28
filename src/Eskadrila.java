import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import pozicijePack.Pozicija;
import pozicijePack.PozicijeLista;
import pozicijePack.Tip;

import java.util.ArrayList;
import java.util.Scanner;

public class Eskadrila {
    public static void main(String[] args) {
        Client klijent = new Client(50000, 50000);
        Kryo kryo = klijent.getKryo();
        kryo.register(Pozicija.class);
        kryo.register(Tip.class);
        kryo.register(Avion.class);
        kryo.register(String.class);
        kryo.register(ArrayList.class);
        kryo.register(PozicijeLista.class);
        klijent.start();

        Scanner scanner = new Scanner(System.in);
        int port;
        double x, y;
        Tip[] mojiTipovi;
        String s;
        while (true) {
            System.out.println("Pokretanje eskradrile: P(Plava), C(Crvena) ");
            s = scanner.nextLine();
            if (s.equals("P")) {
                port = 10000;
                x = 0.5;
                y = 0.5;
                mojiTipovi = new Tip[]{Tip.SU30, Tip.SU35, Tip.MIG31, Tip.SU30, Tip.SU57};
                break;
            } else if (s.equals("C")) {
                port = 20000;
                x = 7.5;
                y = 7.5;
                mojiTipovi = new Tip[]{Tip.F22, Tip.F15, Tip.F35, Tip.F22, Tip.F15};
                break;
            } else {
                System.out.println("Nije dobro slovo");
            }
        }
            try {
                klijent.connect(10000, "127.0.0.1", port);
                System.out.println("Eskadrila povezana na komandni centar! " + port);
                System.out.println("Pokret!");
                int maxAviona = 5;
                for (int i = 0; i < maxAviona; i++) {
                    String sifraAviona = s + i;
                    Pozicija poc = new Pozicija(x, y, mojiTipovi[i], sifraAviona);
                    Avion avion = new Avion(i + 1, sifraAviona, mojiTipovi[i], poc, true, klijent);
                    Thread nitAvion = new Thread(avion);
                    nitAvion.start();
                }
                System.out.println("U letu!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

}
