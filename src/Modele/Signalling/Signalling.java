package Modele.Signalling;

import Modele.Direction;
import Modele.Position;


public abstract class Signalling {
    protected Position position;
    protected Direction direction;
    
    public Signalling(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public abstract String getType();
    public Direction getDirection() {return direction;}
}


