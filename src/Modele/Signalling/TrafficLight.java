package Modele.Signalling;

import Modele.Direction;
import Modele.Position;

public class TrafficLight extends Signalling {
    private LightColor color;

    public TrafficLight(Position pos, Direction direction) {
        super(pos, direction);
        this.color = LightColor.RED; // Initialisé en rouge
    }

    public void setColor(LightColor color) {
        this.color = color;
        System.out.println("Feu à (" + getPosition().getX() + "," + getPosition().getY() + ") [" + direction + "] -> " + color);
    }

    public boolean isNorthSouth() {
        return this.direction == Direction.UP || this.direction == Direction.DOWN;
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
    public String getType() {
        return "TrafficLights";
    }

    @Override
    public String toString(){
        return "Feu à (" + getPosition().getX() + "," + getPosition().getY() + ") [" + direction + "] -> " + color;
    }
}


