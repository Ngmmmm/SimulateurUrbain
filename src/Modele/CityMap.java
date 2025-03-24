package Modele;

import Modele.Signalling.NoEntry;
import Modele.Signalling.Stop;
import Modele.Signalling.TrafficLight;
import Modele.Vehicle.Vehicle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CityMap{
    private int rows;
    private int cols;
    LinkedList<Position> accessiblePosition;

    private Case[][] grid; // Grille représentant la carte


    public CityMap(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.grid = new Case[rows][cols];
        initializeGrid();
    }

    // Initialisation de la grille avec des cellules vides
    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Case();
                grid[i][j].setPos(new Position(j, i));
            }
        }
    }

    public void setRoad(Position pos, String ligne, Direction... d) {
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col)) {
            Road currentRoad = grid[row][col].getRoad();
    
            if (currentRoad != null) {
                // Fusionner les directions existantes avec les nouvelles
                List<Direction> updatedDirections = new ArrayList<>(Arrays.asList(currentRoad.getDirection()));
                currentRoad.setIntersection(true);
                for (Direction newDirection : d) {
                    if (!updatedDirections.contains(newDirection)) {
                        updatedDirections.add(newDirection);
                    }
                }
    
                // Mise à jour des paramètres de la route
                currentRoad.setDirection(updatedDirections.toArray(new Direction[0]));
                currentRoad.setDoubleLane(ligne == "Pointillé");


    
            } else {
                // Créer une nouvelle route avec les directions et paramètres donnés
                Road newRoad = new Road(d, ligne);

                newRoad.setDoubleLane(ligne == "Pointillé");

                grid[row][col].setRoad(newRoad);
            }
        }
    }

    public boolean isEdge(Position pos, Direction dir) {
        Position nextPos = pos.getNextPosition(dir);
        return nextPos == null || !isValidPosition(nextPos.getY(), nextPos.getX());
    }

    public void changeEdge(Position pos, Direction dir) {
        if (isEdge(pos, dir)) {
            // Vérifier si la case contient une route
            Road currentRoad = getRoad(pos);
            if (currentRoad != null) {
                Direction newDirection = null;
    
                // Déterminer la direction à ajouter en fonction de la direction actuelle
                switch (dir.toString()) {
                    case "UP" -> newDirection = Direction.LEFT;
                    case "DOWN" -> newDirection = Direction.RIGHT;
                    case "RIGHT" -> newDirection = Direction.UP;
                    case "LEFT" -> newDirection = Direction.DOWN;
                }
    
                // Ajouter la nouvelle direction si elle n'est pas déjà présente
                if (newDirection != null && !Arrays.asList(currentRoad.getDirection()).contains(newDirection)) {
                    currentRoad.addDirection(newDirection);
                } else {
                    System.out.println("La direction " + newDirection + " est déjà présente ou est invalide.");
                }
            } else {
                System.out.println("Aucune route à la position " + pos + " pour changer l'edge.");
            }
        }
    }

    public void updateAllEdges() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(j, i); // Créer une position à partir des indices
                Road currentRoad = grid[i][j].getRoad(); // Récupérer la route à la position
    
                // Vérifier si la case contient une route
                if (currentRoad != null) {
                    // Vérifier les bords dans les quatre directions
                    if (isEdge(pos, Direction.UP)) {
                        changeEdge(pos, Direction.UP);
                    }
                    if (isEdge(pos, Direction.DOWN)) {
                        changeEdge(pos, Direction.DOWN);
                    }
                    if (isEdge(pos, Direction.LEFT)) {
                        changeEdge(pos, Direction.LEFT);
                    }
                    if (isEdge(pos, Direction.RIGHT)) {
                        changeEdge(pos, Direction.RIGHT);
                    }
                }
            }
        }
    }

    // Ajouter un véhicule à une position donnée s'il y a une route
    public void addVehicle(Vehicle vehicle) {
        Position pos = vehicle.getPosition();
        int row = pos.getY();
        int col = pos.getX();
        
        if (isValidPosition(row, col)) {
            grid[row][col].setVehicle(vehicle);
        
        }
    }

    public boolean isPositionOccupied(Position pos) {
        for (Vehicle v : getAllVehicles()) {  
            if (v.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }
    
    
    public void setStop(Position pos) {
        int row = pos.getY();
        int col = pos.getX();
        Direction dir = getRoad(pos).getDirection()[0];
        if (isValidPosition(row, col) && grid[row][col].getRoad() != null) {
            Stop stopSign = new Stop(pos, dir);
            grid[row][col].setSignalling(stopSign);
            grid[row][col].getRoad().setStopSign(true);
        } else {
            System.out.println("Impossible d'ajouter un Stop ici (position invalide ou pas de route).");
        }
    }

    public void addTrafficLight(TrafficLight trafficLight) {
        Position pos = trafficLight.getPosition();
        int row = pos.getY();
        int col = pos.getX();
        
        if (isValidPosition(row, col) && grid[row][col].getRoad() != null) {
            grid[row][col].setSignalling(trafficLight);
            grid[row][col].getRoad().setTrafficLight(true);
        }
    }

    public void setNoEntry(Position position) {
        Road road = getRoad(position);
        if (road == null) {
            System.out.println("Impossible de placer un NoEntry : pas de route ici !");
            return;
        }

        // Récupérer la première direction de la route et l'inverse
        Direction[] directions = road.getDirection();
        if (directions.length == 0) {
            System.out.println("Impossible d'inverser une direction inexistante !");
            return;
        }

        Direction oppositeDirection = directions[0].getOpposite();
        NoEntry noEntry = new NoEntry(position, oppositeDirection);

        grid[position.getY()][position.getX()].setSignalling(noEntry);
    }


    // Vérifier si une position est valide
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void setBuilding(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].getRoad() == null){
                    grid[i][j] = new Case("Building");
                }
            }
            
        }
    }

    // Affichage simple de la carte en console
    public void displayMap() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void findAccessiblePos(Position p){
        accessiblePosition = Pathfinding.findAllAccessiblePositions(this, p);
    }

    public LinkedList<Position> getAccessiblePosition(){
        return accessiblePosition;
    }

    // Getter pour récupérer la grille
    public Case[][] getGrid() {
        return grid;
    }

    // Vérifie si une position contient une route
    public boolean isRoad(Position pos) {
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col)) {
            return grid[row][col].getRoad() != null;
        }
        return false;
    }

    // Récupère la route à une position donnée
    public Road getRoad(Position pos) {
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col)) {
            return grid[row][col].getRoad();
        }
        return null;
    }

    public static Case getCase(Case[][] grid, Position pos){
        if(pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < 50 && pos.getY() < 50){
            System.out.print(pos.getX());
            System.out.println(pos.getY());
            return grid[pos.getY()][pos.getX()];
        }else{
            return null;
        }
    }

    public int getRows(){return rows;}

    public int getCols(){return cols;}

    public void update(){} // pour remplir l'implémentation en attendant

    public boolean isCarOnPosition(Position pos){
        return grid[pos.getY()][pos.getX()].getVehicle() != null;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Vehicle vehicle = grid[i][j].getVehicle();
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        }
        
        return vehicles;
    }
    public boolean haveBuildingNext(Case eCase) {
        Position pos = eCase.getPos();
    
        for (Position adjacent : pos.posAutour4(this.cols,this.rows)) {
            Case adjacentCase = grid[adjacent.getY()][adjacent.getX()];
            if (adjacentCase != null && adjacentCase.getBuilding() != null) {
                return true;
            }
        }
        return false;
    }



    
}               