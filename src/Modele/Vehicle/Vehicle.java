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
        start();
        followAllPath(stats);
        
    }

    public Vehicle(String id, int speed, Position position, float fuelConso, CityMap map, Position destination, Statistique stats, Controle ctrl) {
        this.ctrl = ctrl;
        this.id = id;
        this.speed = speed;
        this.position = position;

        Road road = map.getRoad(position);
        Direction dir = (road != null && road.getDirection().length > 0)
                        ? road.getDirection()[0]
                        : Direction.RIGHT;
        this.direction = dir;
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
        start();
    }

    public void start() {
        isMoving = true;
    }

    public void stop() {
        isMoving = false;
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

    /**
     * Renvoie si une voiture peut passer ou non sur cette case
     * @param act Case à varifier
     * @return boolean qui indique si une voiture peut passer en fonction des signalisations
     */
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

    /**
     * Essaye de déplacer la voiture à la case suivante de son trajet et renvoie un boolean sur le succès du déplacement
     * @param stats statistiques à mettre à jour selon les issues de la fonction
     * @return boolean représentant le succès de la fonction
     */
    public boolean move(Statistique stats) {
        Case act = getCase(position);
        Case actRight = getCase(rightPos());
        Case next = getCase(nextPosition());
        
        boolean canMoveToNext = canMoveToNextCase(act, next);

        boolean peutAvancer = nextCaseIsValid()
            && checkPrio()
            && (act.getSignalling() == null || signallingOk(act))
            && (actRight == null || actRight.getSignalling() == null || signallingOk(actRight))
            && canMoveToNext;

        if (peutAvancer && (next == null || next.getVehicle() == null)) {
            if (!isMoving){
                isMoving = true;
                stats.isMoving();
            }
            act.setVehicle(null);
            startAnimation(position);
            position = nextPosition();
            Case dest = map.getGrid()[position.getY()][position.getX()];
            dest.setVehicle(this);
            dest.addPassage();
            stats.updateMax(dest);
            return true;
        }

        if ((next != null && next.getVehicle() != null) || !canMoveToNext){
            // Cas bloqué : prochaine case occupée ou conditions non remplies
            LinkedList<Position> temp = Pathfinding.findShortestPath(map, position, destination);
            if (temp != null && !temp.isEmpty() ){
                this.path = temp;
            }
        }
        if (isMoving){
            isMoving = false;
            stats.isStill();
        }
        return false;
    }

    private boolean canMoveToNextCase(Case currentCase, Case nextCase) {
        if (currentCase == null || nextCase == null) {
            return false;
        }

        // Vérifier si la case actuelle a une route qui permet de se déplacer vers la prochaine case
        Road currentRoad = currentCase.getRoad();
        Road nextRoad = nextCase.getRoad();
        if (currentRoad == null || nextRoad == null) {
            return false;
        }

        // Vérifier si la direction de la route actuelle permet de se déplacer vers la prochaine case
        for (Direction dir : currentRoad.getDirection()) {
            Position nextPos = dir.move(currentCase.getPos());
            if (nextPos.equals(nextCase.getPos())) {
                return true;
            }
        }

        return false;
    }

    public Position checkCaseForSignalling(){
        return position.checkPosForSignalling(direction);
    }

    /**
     * Essaye d'avancer la voiture à la prochaine case et répète le déplacement jusqu'à la fin du trajet
     * @param path
     * @param stats
     * @return boolean selon si la voiture a bougé ou non
     */
    public boolean followPath(LinkedList<Position> path, Statistique stats) {
        temps++;
        move = false;
        if (path.isEmpty()) {
            this.getCase(position).setVehicle(null);
            Simulation.spawnRandomCars(1, stats, ctrl);
            stats.delVoiture(temps);
            if (!isMoving){
                stats.isMoving();
            }
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


    public double calculateDistanceToGoal() {
        return path.size();
    }


}
