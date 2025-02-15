package Modele;

import java.util.ArrayList;
import Controle.Update;
import Modele.Vehicle.Vehicle;
import java.util.Arrays;
import java.util.List;

public class CityMap implements Update{
    private int rows;
    private int cols;
    private ArrayList<TuplePosCase> toUpdate = new ArrayList<TuplePosCase>();

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
    public void addVehicle(Vehicle vehicle, Position pos) {
        if (isValidPosition(pos.getY(), pos.getX()) && grid[pos.getY()][pos.getX()].getRoad() != null) {
            grid[pos.getY()][pos.getX()].setVehicle(vehicle);
        }
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

    /*public void update(TrafficSimulatorGUI gui) { 
        
        // enlevez la dernière ligne et l'argument dans la réel implémentation 
        
        // va update toutes les cases qui ont été mises dans la liste des case a update 
        for(TuplePosCase tileAt : toUpdate){
            grid[tileAt.getX()][tileAt.getY()].update(tileAt.getCase());
        }
        toUpdate.clear();
        gui.updateVue();

    }*/

    public void update(){} // pour remplir l'implémentation en attendant


}