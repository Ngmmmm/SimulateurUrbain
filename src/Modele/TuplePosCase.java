package Modele;
public class TuplePosCase {

    private int x;
    private int y;
    private Case tile;

    TuplePosCase(int x, int y, Case t){
        this.x = x;
        this.y = y;
        this.tile = t;
    }

    public int getX(){ return x; }

    public int getY(){ return y; }

    public Case getCase(){ return tile;}

}
