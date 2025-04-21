package Modele.Vehicle;

import Controle.Controle;
import Modele.*;

public class Truck extends Vehicle {
    
    public Truck(String id, int speed, Position position, Direction direction, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        super(id, speed, position, direction, 5, map, destination, stats, ctrl);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
