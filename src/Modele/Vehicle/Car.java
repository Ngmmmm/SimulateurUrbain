package Modele.Vehicle;

import Controle.Controle;
import Modele.*;


public class Car extends Vehicle {
    
    public Car(String id, int speed, Position position, Direction direction, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        super(id, speed, position, direction, 2, map, destination, stats, ctrl);
    }

    public Car(String id, int speed, Position position, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        super(id, speed, position, 2, map, destination, stats, ctrl);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
