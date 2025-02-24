package Modele;

import Modele.Signalling.NoEntry;
import Modele.Signalling.Stop;
import Modele.Signalling.TrafficLight;
import Modele.Vehicle.Vehicle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityMap{
    private int rows;
    private int cols;

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

                for (Direction newDirection : d) {
                    if (!updatedDirections.contains(newDirection)) {
                        updatedDirections.add(newDirection);
                    }
                }

                // Mettre à jour la route existante avec les directions fusionnées
                currentRoad.setDirection(updatedDirections.toArray(new Direction[0]));
            } else {
                // Créer une nouvelle route avec les directions données
                grid[row][col].setRoad(new Road(d, ligne));
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
            System.out.println("Stop ajouté à la position " + pos + " avec la direction " + dir);
        } else {
            System.out.println("Impossible d'ajouter un Stop ici (position invalide ou pas de route).");
        }
    }

    public void addTrafficLight(TrafficLight trafficLight) {
        Position pos = trafficLight.getPosition();
        int row = pos.getY();
        int col = pos.getX();
        
        if (isValidPosition(row, col)) {
            grid[row][col].setSignalling(trafficLight);
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
        System.out.println("NoEntry placé en " + position + " direction " + oppositeDirection);
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
    public int getRows(){return rows;}

    public int getCols(){return cols;}

    public void update(){} // pour remplir l'implémentation en attendant

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

    
}