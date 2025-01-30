package Modele.Signalling;

import Modele.Position;

public class Signalling {


    private Position pos;

    public Signalling(Position pos){
        this.pos = pos;
        
    }
    
    public Position getPos() {
        return this.pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

}
