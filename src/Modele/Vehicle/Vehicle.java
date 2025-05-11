package Modele.Vehicle;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.swing.Timer;


public abstract class Vehicle {
    protected String id;
    protected int speed;
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
    public LinkedList<Position> path;
    boolean move;
    protected int temps = 0;
    protected int timeStanding = 0;
    private Position oldPosition;               // ancienne position
    private float animationProgress = 1.0f;
    protected Controle ctrl;

    public Vehicle(String id, int speed, Position position, Direction direction, float fuelConso, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        this.ctrl = ctrl;
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
        stats.addVoiture();
        followAllPath(stats);
        
    }

    public Vehicle(String id, int speed, Position position, float fuelConso, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        this.ctrl = ctrl;
        this.id = id;
        this.speed = speed;
        this.position = position;
        this.direction = Direction.UP;
        this.isMoving = false;
        this.fuelConso = fuelConso;
        this.fuel = 100;
        this.map = map;
        this.destination = destination;
        nbCar++;
        if (map != null){
            map.addVehicle(this);
        }
        stats.addVoiture();
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
    public void setSpeed(int speed) {this.speed = speed;}
    public LinkedList<Position> getPath(){return this.path;}


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
        if (direction == null) {
            return null; // Retourner null si la direction est null
        }
        return direction.move(position);
    }
    public Position rightPos(){
        if(position.getX() > 0 && position.getY() > 0 && direction != null){
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
        return CityMap.getCase(map.getGrid(), pos);
        
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
            if(signal.getDirection().toString().equals(direction.toString())){
                if(signal instanceof TrafficLight trafficLights){
                    if (trafficLights.getColor().equals("RED") || trafficLights.getColor().equals("ORANGE")){
                        return false;
                    }
                }else if(signal instanceof Stop){
                    Position targetPosition = checkCaseForSignalling();
                    Case caseToCheck = getCase(targetPosition);
                    if (caseToCheck != null) {
                        Timer timer = new Timer(200 * ctrl.getSpeed(), (ActionEvent e) -> {
                            this.stopOk = caseToCheck.isEmpty();
                        });
                    
                        timer.setRepeats(false); 
                        timer.start();
                        return this.stopOk;
                    } else {
                        // Position non valide : on considère par défaut qu'on ne peut pas avancer
                        return false;
                    }

                }else return !(signal instanceof NoEntry);
            }
        }
        return true;
    }

    public boolean move(Statistique stats) {
        Case act = getCase(position);
        Case actRight = getCase(rightPos());
        Case next = getCase(nextPosition());

        boolean peutAvancer = nextCaseIsValid()
            && checkPrio()
            && (act.getSignalling() == null || signallingOk(act))
            && (actRight == null || actRight.getSignalling() == null || signallingOk(actRight));

        if (peutAvancer && (next == null || next.getVehicle() == null)) {
            act.setVehicle(null);
            startAnimation(position);
            position = nextPosition();
            Case dest = map.getGrid()[position.getY()][position.getX()];
            dest.setVehicle(this);
            dest.addPassage();
            stats.updateMax(dest);
            return true;
        }

        if (next != null && next.getVehicle() != null){
            // Cas bloqué : prochaine case occupée ou conditions non remplies
            LinkedList<Position> temp = Pathfinding.findShortestPath(map, position, destination);
            if (temp != null && !temp.isEmpty() ){
                this.path = temp;
                //System.out.println("Recalcul de chemin pour " + position + " vers " + destination);
                return true;
            }
        }
        return false;
    }



    public Position checkCaseForSignalling(){
        return position.checkPosForSignalling(direction);
    }



    public boolean followPath(LinkedList<Position> path, Statistique stats) {
        temps++;
        move = false;
        if (path.isEmpty()) {
            this.getCase(position).setVehicle(null);
            Simulation.spawnRandomCars(1, stats, ctrl);
            stats.delVoiture(temps);
            return false; // Arrêt si on a atteint la destination
        }
    
        Position prochainePos = path.pop();
        if (position == prochainePos) {
            ctrl.addUpdateCar(position);
            return false;
        }
        Direction prochaineDir = Direction.getDirection(position, prochainePos);
        changeDirection(prochaineDir);
        Timer timer = new Timer(this.speed * 50 * ctrl.getSpeed(), (ActionEvent e) -> {
            move = move(stats);
            if(!move){
                path.push(prochainePos);
                timeStanding++;
            }else{
                stats.isMoving();
                stats.addArret(timeStanding);
                timeStanding = 0;
            }
            ctrl.addUpdateCar(position);
        });

        timer.setRepeats(false); 
        timer.start();

        return move;
    
    }
    
    public void followAllPath(Statistique stats) {
        checkPath();
        if (!path.isEmpty()) {
            path.pop(); // Supprimer le premier élément de la liste
        }
        if (!path.isEmpty()) {
            followPath(path, stats); 
        }
    }

    public void checkPath(){
        path = Pathfinding.findShortestPath(map, position, destination);
    }


    
    public Position getDestination() {
        return destination;
    }


    public boolean checkPrio(){
        Case right = getCase(rightPos());
        if(right != null && right.haveBuilding()){
            Case prio = getCase(checkCaseForSignalling());
            return !prio.haveVehicle();
        }else{
            return true;
        }
        

    }
    public void startAnimation(Position fromPosition) {
        this.oldPosition = fromPosition;
        this.animationProgress = 0.0f;
    }
    
    public void updateAnimationProgress(float delta) {
        animationProgress = Math.min(1.0f, animationProgress + delta );
    }
    
    public boolean isMovingTransition() {
        return animationProgress < 1.0f;
    }
    
    public float getAnimationProgress() {
        return animationProgress;
    }
    
    public Position getOldPosition() {
        return oldPosition != null ? oldPosition : position;
    }


}
