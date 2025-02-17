package Modele.Vehicle;

import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Pathfinding;
import Modele.Position;
import Modele.Signalling.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.Timer;

import Controle.*;

public abstract class Vehicle {
    protected String id;
    protected double speed;
    protected Position position;
    protected Direction direction;
    protected boolean isMoving;
    protected float fuel;
    protected float fuelConso;
    protected BufferedImage img;
    protected CityMap map;
    protected Position destination;

    public Vehicle(String id, double speed, Position position, Direction direction, float fuelConso, CityMap map, Position destination) {
        this.id = id;
        this.speed = speed;
        this.position = position;
        this.direction = direction;
        this.isMoving = false;
        this.fuelConso = fuelConso;
        this.fuel = 100;
        this.map = map;
        this.destination = destination;
    }

    public void start() {
        isMoving = true;
    }

    public void stop() {
        isMoving = false;
    }

    public void move1(Direction d) {
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
        return map.getGrid()[pos.getY()][pos.getX()];
    }

    public boolean nextCaseIsValid(){
        Case next = getCase(nextPosition());
        return !next.isEmpty();
    }

    public boolean signallingOk(Case act){
        Signalling signal = act.getSignalling();
        if(signal != null){
            if(signal instanceof TrafficLight trafficLights){
                System.out.println("FEUDANSLACASDE");
                if (trafficLights.getColor().equals("RED") || trafficLights.getColor().equals("ORANGE")){
                    return false;
                }
            }else if(signal instanceof Stop){
                if(checkCaseForSignalling() != null){
                    if(getCase(checkCaseForSignalling()).isEmpty()){
                        Timer timer = new Timer(1000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                            }
                        });
                        timer.setRepeats(false); // S'assure que le timer ne se répète pas
                        timer.start();
                        return getCase(checkCaseForSignalling()).isEmpty();
                    }
                }

            }else return !(signal instanceof NoEntry);
        }
        return true;
    }

    public boolean move(){
        Case act = getCase(position);
        if (nextCaseIsValid()){
            if(act.getSignalling() == null || signallingOk(act)){
                act.setVehicle(null);
                Controle.addUpdate(new TuplePosAct(position, new Update(){public void update(Case c){}}));
                position = nextPosition();
                map.getGrid()[position.getY()][position.getX()].setVehicle(this);
                return true;
            }
        }
        return false;
    }

    public Position checkCaseForSignalling(){
        return position.checkPosForSignalling(direction);
    }



    private void followPath(ArrayList<Position> path, int index) {
        if (index >= path.size()) {
            return; // Arrêt si on a atteint la destination
        }
    
        Position prochainePos = path.get(index);
        Direction prochaineDir = Direction.getDirection(position, prochainePos);
        changeDirection(prochaineDir);
    
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print(position.getX());
                System.out.println(position.getY());
                if(move()){
                    followPath(path, index + 1); 
                    Controle.addUpdate(new TuplePosAct(position, new Update(){public void update(Case c){}}));
                }else{
                    followPath(path, index);
                }
                
                
            }
        });
    
        timer.setRepeats(false); 
        timer.start();
    }
    
    public void followAllPath() {
        ArrayList<Position> path = Pathfinding.findShortestPath(map, position, destination);
        if (path.size() > 1) {
            followPath(path, 1); 
        }
    }

    

}
