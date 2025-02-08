package Modele;

import Modele.Vehicle.Vehicle;
import java.util.ArrayList;
import java.util.List;


public class CityMap {
    private int rows;
    private int cols;
    private Case[][] grid; // Grille représentant la carte


    public CityMap(int rows, int cols) {
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
            
            // Si la route existe déjà, on fusionne les directions
            if (currentRoad != null) {
                List<Direction> updatedDirections = new ArrayList<>();
                for (Direction existingDirection : currentRoad.getDirection()) {
                    updatedDirections.add(existingDirection);
                }
                for (Direction newDirection : d) {
                    // On ajoute la nouvelle direction si elle n'est pas déjà présente
                    if (!updatedDirections.contains(newDirection)) {
                        updatedDirections.add(newDirection);
                    }
                }
                // Mise à jour de la route avec les directions fusionnées
                currentRoad.setDirection(updatedDirections.toArray(new Direction[0]));
            } else {
                // Sinon, on crée une nouvelle route avec les directions données
                grid[row][col].setRoad(new Road(d, ligne));
            }
        }
    }


    // Ajouter un véhicule à la carte
    public void addVehicle(Vehicle vehicle) {
        
    }

    // Vérifier si une position est valide
    private boolean isValidPosition(int row, int col) {
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

}