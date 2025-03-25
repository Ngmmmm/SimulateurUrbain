package Modele.Vehicle;

import Modele.CityMap;
import Modele.Direction;
import Modele.Position;
import Modele.Statistique;

public class Truck extends Vehicle {
    
    public Truck(String id, int speed, Position position, Direction direction, CityMap map, Position destination, Statistique stats) {
        super(id, speed, position, direction, 5, map, destination, stats);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
