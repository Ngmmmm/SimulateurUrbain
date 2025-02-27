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
    public boolean stopOk;
    public static int nbCar = 0;

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
        nbCar++;
        if (map != null){
            map.addVehicle(this);
        }
        followAllPath();

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
    public Position rightPos(){
        if(position.getX() > 0 && position.getY() > 0){
        switch (direction.toString()) {
            case "UP" -> {
                return new Position(this.position.getX() +1, this.position.getY());
            }
            case "LEFT" -> {
                return new Position(this.position.getX(), this.position.getY() -1);
            }
            case "RIGHT" -> {
                return new Position(this.position.getX(), this.position.getY() +1);
            }
            case "DOWN" -> {
                return new Position(this.position.getX() -1, this.position.getY());
            }
            default -> {
                return null;
            }
        }
        }
        return position;
    }

    public void tourner(Direction direction){
        this.direction = direction;
    }

    public Case getCase(Position pos){
        if(pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < 50 && pos.getY() < 50){
            System.out.print(pos.getX());
            System.out.println(pos.getY());
            return map.getGrid()[pos.getY()][pos.getX()];
        }else{
            return null;
        }
        
    }

    public int getNbCar(){
        return nbCar;
    }


    public boolean nextCaseIsValid(){
        Case next = getCase(nextPosition());
        return next != null && next.isEmpty();
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
                        Timer timer = new Timer(2000, (ActionEvent e) -> {
                            this.stopOk = getCase(checkCaseForSignalling()).isEmpty();
                        });
                    
                        timer.setRepeats(false); 
                        timer.start();
                        return this.stopOk;
                }

            }else return !(signal instanceof NoEntry);
        }
        return true;
    }

    public boolean move(){
        Case act = getCase(position);
        Case actRight = getCase(rightPos());
        if (nextCaseIsValid()){
            if((act.getSignalling() == null || signallingOk(act)) && (actRight == null ||actRight.getSignalling() == null || signallingOk(actRight))){
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
            this.getCase(position).setVehicle(null);
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
