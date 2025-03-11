package Modele.Vehicle;

import Modele.CityMap;
import Modele.Direction;
import Modele.Position;

public class Bus extends Vehicle {

    public Bus(String id, int speed, Position position, Direction direction, CityMap map, Position destination) {
        super(id, speed, position, direction, 4, map, destination);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
