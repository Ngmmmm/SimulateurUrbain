package Controle;

import Modele.Position;

public class TuplePosAct {

    private int x;
    private int y;
    private Update act;

    public TuplePosAct(int x, int y, Update act){
        this.x = x;
        this.y = y;
        this.act = act;
    }

    public TuplePosAct(Position pos, Update act){
        this.x = pos.getX();
        this.y = pos.getY();
    }

    public int getX(){ return x; }

    public int getY(){ return y; }

    public Update getAct(){ return act;}

}
