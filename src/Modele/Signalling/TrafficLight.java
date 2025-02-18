package Modele.Signalling;

import Controle.Controle;
import Controle.TuplePosAct;
import Controle.Update;
import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Position;

public class TrafficLight extends Signalling {
    private LightColor color;
    private Direction direction;

    public TrafficLight(Position pos, Direction direction, CityMap map) {
        super(pos);
        this.direction = direction;
        this.color = LightColor.RED; // Initialisé en rouge
        if (map != null){
            map.addTrafficLight(this);
        }

    }

    public void setColor(LightColor color) {
        this.color = color;
        System.out.println(this);
        Controle.addUpdate(new TuplePosAct(position, new Update(){public void update(Case c){}}));
    }

    public boolean isNorthSouth() {
        return this.direction == Direction.UP ||
        this.direction == Direction.DOWN;
    }

    public Direction getDirection() { return direction; }
    public Position getPosition() { return position; }
    public String getColor() {return color.name();}
       
    @Override
    public String getType() {return "TrafficLights";}

    @Override
    public String toString(){
        return "Feu à (" + 
            getPosition().getX() + "," + 
            getPosition().getY() + ") [" + direction + "] -> " +
            color;
    }


    public LightColor getCol(){
        return color;
    }
}


