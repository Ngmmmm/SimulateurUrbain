package Modele;

import java.util.ArrayList;

import Modele.Vehicle.Vehicle;
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



    // Ajouter un véhicule à la carte
    public void addVehicle(Vehicle vehicle) {
        
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


    public void update(){} // pour remplir l'implémentation en attendant


}