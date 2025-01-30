package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public class Car extends Vehicle {
    private int numberOfDoors;

    public Car(String id, double speed, Position position, Direction direction, int numberOfDoors) {
        super(id, speed, position, direction);
        this.numberOfDoors = numberOfDoors;
    }


    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    @Override
    public String toString() {
        return super.toString() + ", numberOfDoors=" + numberOfDoors + '}';
    }
}
