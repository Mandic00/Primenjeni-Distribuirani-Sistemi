package pozicijePack;

public enum Tip {
    MIG31(20,7.0),

    F15(15,3.0),
    F22(15,6.1),
    SU30(13,4.0),
    SU35(13,6.0),
    SU57(12,5.5),

    F35(10,5.6),

    RAKETA(30,1.0);
    private final int brzina;
    private final double dometRadara;

    Tip(int brzina, double dometRadara) {
        this.brzina = brzina;
        this.dometRadara = dometRadara;
    }
    public int getBrzina() {
        return brzina;
    }
    public double getDometRadara() {
        return dometRadara;
    }

}
