package Modele.Vehicle;

import Modele.CityMap;
import Modele.Direction;
import Modele.Position;

public class Truck extends Vehicle {
    
    public Truck(String id, double speed, Position position, Direction direction, int numberOfDoors, CityMap map, Position destination) {
        super(id, speed, position, direction, 5, map, destination);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
