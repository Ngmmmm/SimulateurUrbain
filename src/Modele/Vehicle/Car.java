package Modele.Vehicle;

import Modele.*;


public class Car extends Vehicle {
    
    public Car(String id, int speed, Position position, Direction direction, CityMap map, Position destination, Statistique stats) {
        super(id, speed, position, direction, 2, map, destination, stats);
    }

    public Car(String id, int speed, Position position, CityMap map, Position destination, Statistique stats) {
        super(id, speed, position, 2, map, destination, stats);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
