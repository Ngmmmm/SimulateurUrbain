package Modele;

import java.util.Arrays;

public class Road {
    private Direction[] direction;
    private String lineType;
    private boolean isIntersection = false;
    
    // Nouveaux attributs
    private double weight = 1.0; // Coût de base par défaut
    private double trafficFactor = 1.0; // Facteur de congestion (1 = normal, >1 = embouteillé)
    private boolean hasTrafficLight = false;
    private boolean hasStopSign = false;
    private int trafficLightDelay = 2; // Temps d’attente moyen à un feu
    private boolean isDoubleLane = false;

    public Road(Direction[] direction, String lineType) {
        this.direction = direction;
        this.lineType = lineType;
    }

    // Getters & Setters
    public Direction[] getDirection() { return direction; }
    public void setDirection(Direction[] direction) { this.direction = direction; }

    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }

    public boolean isIntersection() { return isIntersection; }
    public void setIntersection(boolean isIntersection) { this.isIntersection = isIntersection; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getTrafficFactor() { return trafficFactor; }
    public void setTrafficFactor(double trafficFactor) { this.trafficFactor = trafficFactor; }

    public boolean hasTrafficLight() { return hasTrafficLight; }
    public void setTrafficLight(boolean hasTrafficLight) { this.hasTrafficLight = hasTrafficLight; }

    public int getTrafficLightDelay() { return trafficLightDelay; }
    public void setTrafficLightDelay(int trafficLightDelay) { this.trafficLightDelay = trafficLightDelay; }

    public boolean hasStopSign() { return hasStopSign; }
    public void setStopSign(boolean hasStopSign) { this.hasStopSign = hasStopSign; }

    public boolean isDoubleLane() { return isDoubleLane; }
    public void setDoubleLane(boolean isDoubleLane) { this.isDoubleLane = isDoubleLane; }

    // Méthode pour ajouter une direction
    public void addDirection(Direction newDirection) {
        if (!Arrays.asList(direction).contains(newDirection)) {
            direction = Arrays.copyOf(direction, direction.length + 1); // Créer un nouveau tableau
            direction[direction.length - 1] = newDirection; // Ajouter la nouvelle direction
        }
    }

    @Override
    public String toString() {
        return "Route";
    }
}
