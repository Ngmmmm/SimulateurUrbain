package Modele.Vehicle;

import Modele.Case;
import Modele.Direction;
import Modele.Position;

public class Truck extends Vehicle {
    
    public Truck(String id, double speed, Position position, Direction direction, int numberOfDoors, Case[][] map) {
        super(id, speed, position, direction, 5, map);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
