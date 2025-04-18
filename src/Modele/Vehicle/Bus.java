package Modele.Vehicle;

import Modele.*;

public class Bus extends Vehicle {

    public Bus(String id, int speed, Position position, Direction direction, CityMap map, Position destination, Statistique stats) {
        super(id, speed, position, direction, 4, map, destination, stats);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
