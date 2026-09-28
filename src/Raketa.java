import pozicijePack.Pozicija;
import pozicijePack.Tip;

public class Raketa implements Runnable{
    private Pozicija trenutna;
    private Pozicija meta;
    private final Tip tip = Tip.RAKETA;

    public Raketa(Pozicija trenutna, Pozicija meta) {
        this.trenutna = trenutna;
        this.meta = meta;
    }

    @Override
    public void run() {

    }
}
