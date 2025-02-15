package Modele.Vehicle;

import Modele.Case;
import Modele.Direction;
import Modele.Position;

public class Car extends Vehicle {
    
    public Car(String id, double speed, Position position, Direction direction, Case[][] map, Position destination) {
        super(id, speed, position, direction, 2, map, destination);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
