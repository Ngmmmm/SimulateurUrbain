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

    public void setIntersection(boolean b) {
        isIntersection = b;
    }

    public boolean isIntersection() {
        return isIntersection;
    }

    @Override
    public String toString() {
        return "Route";
    }

    public void addDirection(Direction newDirection) {
        if (!Arrays.asList(direction).contains(newDirection)) { // Vérifier si la direction est déjà présente
            Direction[] newDirections = Arrays.copyOf(direction, direction.length + 1); // Créer un nouveau tableau
            newDirections[newDirections.length - 1] = newDirection; // Ajouter la nouvelle direction
            this.direction = newDirections;
        }
    }

    // --- Nouveaux getters/setters pour la pondération et le trafic ---
    
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTrafficFactor() {
        return trafficFactor;
    }

    public void setTrafficFactor(double trafficFactor) {
        this.trafficFactor = trafficFactor;
    }

    public boolean hasTrafficLight() {
        return hasTrafficLight;
    }

    public void setTrafficLight(boolean hasTrafficLight) {
        this.hasTrafficLight = hasTrafficLight;
    }

    public int getTrafficLightDelay() {
        return trafficLightDelay;
    }

    public void setTrafficLightDelay(int trafficLightDelay) {
        this.trafficLightDelay = trafficLightDelay;
    }

    public boolean hasStopSign() {
        return hasStopSign;
    }

    public void setStopSign(boolean hasStopSign) {
        this.hasStopSign = hasStopSign;
    }

    public boolean isDoubleLane() {
        return isDoubleLane;
    }

    public void setDoubleLane(boolean isDoubleLane) {
        this.isDoubleLane = isDoubleLane;
    }
}
