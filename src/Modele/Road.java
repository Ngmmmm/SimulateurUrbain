package Modele;

import java.util.Arrays;

public class Road {
    private Direction[] direction;
    private String lineType;

    public Road(Direction[] direction, String lineType) {
        this.direction = direction;
        this.lineType = lineType;
    }

    public Direction[] getDirection() {
        return direction;
    }

    public void setDirection(Direction[] direction) {
        this.direction = direction;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    @Override
    public String toString(){
        return "Route";
    }

    public void addDirection(Direction newDirection) {
        if (!Arrays.asList(direction).contains(newDirection)) { // Vérifier si la direction est déjà présente
            Direction[] newDirections = Arrays.copyOf(direction, direction.length + 1); // Créer un nouveau tableau
            newDirections[newDirections.length - 1] = newDirection; // Ajouter la nouvelle direction
            this.direction = newDirections;
        } else {
            System.out.println("La direction " + newDirection + " est déjà présente.");
        }
    }

}
