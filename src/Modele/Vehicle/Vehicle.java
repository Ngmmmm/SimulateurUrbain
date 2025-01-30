package Modele.Vehicle;

import Modele.Direction;
import Modele.Position;

public abstract class Vehicle {
    protected String id;
    protected double speed;
    protected Position position;
    protected Direction direction;
    protected boolean isMoving;
    protected float fuel;

    public Vehicle(String id, double speed, Position position, Direction direction) {
        this.id = id;
        this.speed = speed;
        this.position = position;
        this.direction = direction;
        this.isMoving = false;
        this.fuel = 100;
    }

    public void start() {
        isMoving = true;
    }

    public void stop() {
        isMoving = false;
    }

    public void move(Direction d) {
        changeDirection(d);
        position = d.move(position);
    }

    public void changeDirection(Direction newDirection) {
        this.direction = newDirection;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Position getPosition() {
        return position;
    }


    public Direction getDirection() {
        return direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", speed=" + speed +
                ", positionX=" + position.getX()+
                ", positionY=" + position.getY() +
                ", direction=" + direction +
                ", isMoving=" + isMoving +
                '}';
    }
}
