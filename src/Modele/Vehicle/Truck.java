package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public class Truck extends Vehicle {
    
    public Truck(String id, double speed, Position position, Direction direction, int numberOfDoors, float fuelConso) {
        super(id, speed, position, direction, 5);
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
        return super.toString();
    }
}
