package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public class Truck extends Vehicle {
    
    public Truck(String id, double speed, Position position, Direction direction, int numberOfDoors) {
        super(id, speed, position, direction, 5);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
