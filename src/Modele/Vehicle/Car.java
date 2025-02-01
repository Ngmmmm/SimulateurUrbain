package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public class Car extends Vehicle {
    
    public Car(String id, double speed, Position position, Direction direction, int numberOfDoors) {
        super(id, speed, position, direction, 2);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
