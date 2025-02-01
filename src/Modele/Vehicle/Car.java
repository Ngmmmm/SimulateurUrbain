package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public class Car extends Vehicle {
    
    public Car(String id, double speed, Position position, Direction direction, int numberOfDoors, float fuelConso) {
        super(id, speed, position, direction, 2);
    }

    @Override
    public void move(Direction d) {
        if (fuel > 0) {
            super.move(d);
            consumeFuel();
        } else {
            System.out.println("No fuel");
            stop();
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", fuel=" + fuel;
    }
}
