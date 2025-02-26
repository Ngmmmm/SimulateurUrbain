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
    private int idGroupe;

    public TrafficLight(Position pos, Direction direction, CityMap map) {
        super(pos, direction);
        this.color = LightColor.RED; // Initialisé en rouge
        if (map != null){
            map.addTrafficLight(this);
        }

    }

    public void setColor(LightColor color) {
        this.color = color;
        //System.out.println(this);
        Controle.addUpdate(new TuplePosAct(position, new Update(){public void update(Case c){}}));
    }

    public boolean isNorthSouth() {
        return this.direction == Direction.UP ||
        this.direction == Direction.DOWN;
    }
    public String getColor() {
        return color.name();
    }
    public Direction getDirection() { return direction; }
    @Override
    public Position getPosition() {
        return position;
    }
       
    @Override
    public String getType() {return "TrafficLights";}

    @Override
    public String toString(){
        return "Feu à (" + 
            getPosition().getX() + "," + 
            getPosition().getY() + ") [" + direction + "] -> " +
            color;
    }


    public void setIDGroupe(int id) {
        this.idGroupe = id;
    }

    public int getIDGroupe(){
        return idGroupe;
    }

    public LightColor getCol(){
        return color;
    }
}


