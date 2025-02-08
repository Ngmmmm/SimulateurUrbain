package Modele;

import java.util.ArrayList;

import Controle.Update;
import Modele.Vehicle.Vehicle;
import Vue.TrafficSimulatorGUI; // à enlever


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

    // Ajouter une route à la carte
    public void setRoad(Position pos, Direction d, String ligne) {
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col)) {
            grid[row][col].setRoad(new Road(d, ligne));
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

    public void update(TrafficSimulatorGUI gui) { 
        
        // enlevez la dernière ligne et l'argument dans la réel implémentation 
        
        // va update toutes les cases qui ont été mises dans la liste des case a update 
        for(TuplePosCase tileAt : toUpdate){
            grid[tileAt.getX()][tileAt.getY()].update(tileAt.getCase());
        }
        toUpdate.clear();
        gui.updateVue();

    }

    public void update(){} // pour remplir l'implémentation en attendant


}