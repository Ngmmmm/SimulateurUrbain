package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public class Bus extends Vehicle {

    public Bus(String id, double speed, Position position, Direction direction, int numberOfDoors) {
        super(id, speed, position, direction, 4);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
