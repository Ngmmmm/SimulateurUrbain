package Modele.Signalling;

import Modele.Direction;
import Modele.Position;

public class TrafficLight extends Signalling {
    private LightColor color;
    private Direction direction;

    public TrafficLight(Position pos, Direction direction) {
        super(pos);
        this.direction = direction;
        this.color = LightColor.RED; // Initialisé en rouge
    }

    public void setColor(LightColor color) {
        this.color = color;
        System.out.println("Feu à (" + getPosition().getX() + "," + getPosition().getY() + ") [" + direction + "] -> " + color);
    }

    public boolean isNorthSouth() {
        return this.direction == Direction.UP || this.direction == Direction.DOWN;
    }

    public Direction getDirection() { return direction; }
    public Position getPosition() {
        return position;
    }
       
    @Override
    public String getType() {
        return "TrafficLights";
    }

    @Override
    public String toString(){
        return "Feu à (" + getPosition().getX() + "," + getPosition().getY() + ") [" + direction + "] -> " + color;
    }
}


