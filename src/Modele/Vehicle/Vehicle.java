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
    protected float fuelConso;

    public Vehicle(String id, double speed, Position position, Direction direction, float fuelConso) {
        this.id = id;
        this.speed = speed;
        this.position = position;
        this.direction = direction;
        this.isMoving = false;
        this.fuelConso = fuelConso;
        this.fuel = 100;
    }

    public void start() {
        isMoving = true;
    }

    public void stop() {
        isMoving = false;
    }

    public void move(Direction d) {
        if (fuel > 0) {
            changeDirection(d);
            position = d.move(position);
            consumeFuel();
        } else {
            System.out.println("No fuel");
            stop();
        }
    }

    public void changeDirection(Direction newDirection) {
        this.direction = newDirection;
    }

    public void consumeFuel() {
        fuel -= fuelConso;
        if (fuel < 0) {
            fuel = 0;
        }
    }

    public void refuel(float quantity) {
        fuel += quantity;
        if (fuel > 100) {
            fuel = 100;
        }
    }

    /*
    public void accelerate(double amount) {
        this.speed += amount;
    }
    
    public void brake(double amount) {
        this.speed -= amount;
        if (this.speed < 0) {
            this.speed = 0;
        }
    }
    */

    public float getFuel() {return this.fuel;}
    public String getId() {return this.id;}
    public double getSpeed() {return this.speed;}
    public Position getPosition() {return this.position;}
    public Direction getDirection() {return this.direction;}
    public boolean isMoving() {return this.isMoving;}
    public void setSpeed(double speed) {this.speed = speed;}


    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", speed=" + speed +
                ", position=(" + position.getX() + ", " + position.getY() + ")" +
                ", direction=" + direction +
                ", isMoving=" + isMoving +
                ", fuel=" + fuel + "/" + 100 +
                ", fuelConsumption=" + fuelConso +
                '}';
    }

}
