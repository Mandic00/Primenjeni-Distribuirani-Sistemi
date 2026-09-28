package pozicijePack;

import java.io.Serializable;

public class Pozicija implements Serializable {
    private double x;
    private double y;
    private Tip tip;
    private String imeAviona;

    public Pozicija(double x, double y, Tip tip, String imeAviona) {
        this.x = x;
        this.y = y;
        this.tip = tip;
        this.imeAviona = imeAviona;
    }
    public Pozicija() {

    }

    public String getImeAviona() {
        return imeAviona;
    }

    public void setImeAviona(String imeAviona) {
        this.imeAviona = imeAviona;
    }

    public Tip getTip() {
        return tip;
    }

    public void setTip(Tip tip) {
        this.tip = tip;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double euklidskoRastojanje(Pozicija dva) {
        double dx = dva.getX() - this.x;
        double dy = dva.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy);

    }


}
