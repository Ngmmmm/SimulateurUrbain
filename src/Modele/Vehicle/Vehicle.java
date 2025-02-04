package Modele.Vehicle;

import Modele.Case;
import Modele.Direction;
import Modele.Position;
import Modele.Signalling.NoEntry;
import Modele.Signalling.Signalling;
import Modele.Signalling.Stop;
import Modele.Signalling.TrafficLights;
import java.awt.image.BufferedImage;

public abstract class Vehicle {
    protected String id;
    protected double speed;
    protected Position position;
    protected Direction direction;
    protected boolean isMoving;
    protected float fuel;
    protected float fuelConso;
    protected BufferedImage img;
    protected Case[][] map;

    public Vehicle(String id, double speed, Position position, Direction direction, float fuelConso, Case[][] map) {
        this.id = id;
        this.speed = speed;
        this.position = position;
        this.direction = direction;
        this.isMoving = false;
        this.fuelConso = fuelConso;
        this.fuel = 100;
        this.map = map;
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



    public Position nextPosition(){
        return direction.move(position);
    }

    public void tourner(Direction direction){
        this.direction = direction;
    }

    public Case getCase(Position pos){
        return map[pos.getX()][pos.getY()];
    }

    public boolean nextCaseIsValid(){
        Case next = getCase(nextPosition());
        return next.isEmpty();
    }

    public boolean signallingOk(Case act){
        Signalling signal = act.getSignalling();
        if(signal != null){
            if(signal instanceof TrafficLights trafficLights){
                if (trafficLights.getColor().equals("RED")){
                    return false;
                }
            }else if(signal instanceof Stop){
                if(checkCaseForSignalling() != null){
                    if(getCase(checkCaseForSignalling()).isEmpty()){
                        return true;
                    }
                }

            }else return !(signal instanceof NoEntry);
        }
        return true;
    }

    public void moove(){
        Case act = getCase(position);
        if (nextCaseIsValid()){
            if(act.getSignalling() == null || signallingOk(act)){
                act.setVehicle(null);
                position = nextPosition();
                map[position.getX()][position.getY()].setVehicle(this);
            }
        }
    }

    public Position checkCaseForSignalling(){
        return position.checkPosForSignalling(direction);
    }

    

}
