package Modele.Vehicle;

import Controle.Controle;
import Modele.*;

public class Bus extends Vehicle {

    public Bus(String id, int speed, Position position, Direction direction, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        super(id, speed, position, direction, 4, map, destination, stats, ctrl);
    }

    public Bus(String id, int speed, Position position, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        super(id, speed, position, 4, map, destination, stats, ctrl);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
