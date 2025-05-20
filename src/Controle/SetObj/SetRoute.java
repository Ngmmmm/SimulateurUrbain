package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Vue.TrafficSimulatorGUI;


/**
 * La SetRoute implémente l'interface Update et contient les fonctions d'ajout de Road
 * 
 * 
 * <p> 
 * Cette classe contient des méthodes pour :
 * </p>
 * 
 * <ul>
 * <li> Stocker les directions à ajouter {@link #setSelectedDirections(Direction[])}.</li>
 * <li> Appliquer les changements liés aux Road {@link #update(CityMap, Stack2, Statistique)}. </li>
 * <li> Ajouter les directions supplémentaire à une case {@link #updateExistingRoad(Case)}. </li>
 * <li> Remplacer un Building par un Road {@link #createNewRoad(Case)}</li>
 * </ul>
 */



public class SetRoute implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetRoute set;
    private static Direction[] selectedDirections = null;
    private static boolean intersectionFlag = false;

    public SetRoute(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    
    /** 
     * Fonction qui va stocker l'ensemble des directions à ajouter
     * @param directions Directions à ajouter
     */
    public static void setSelectedDirections(Direction[] directions) {
        selectedDirections = directions;
    }

    
    /** 
     * Fonction qui va changer un Building en Road ou ajouter des directions
     * @param map carte sur laquel ajouter la Road
     * @param c pile contenant la case à modifier et ses informations
     * @param stats objet contenant les informations liées au statistique pour le mettre à jour
     */
    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case target = CityMap.getCase(map.getGrid(), c.getA());

        if (target.getRoad() != null) {
            updateExistingRoad(target);
        } else {
            createNewRoad(target);
        }

        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    
    /** 
     * Fonction qui va ajouter des directions à une Road existante
     * @param target Case sur laquel ajouter les directions
     */
    private void updateExistingRoad(Case target) {
        if (selectedDirections != null && selectedDirections.length > 0) {
            target.getRoad().setDirection(selectedDirections);
            target.getRoad().setIntersection(SetRoute.isIntersection());
            selectedDirections = null;
        }
        
    }

    
    /** 
     * Fonction qui va créer une nouvelle Road si un Building est présent
     * @param target Case sur laquel créer la Road
     */
    private void createNewRoad(Case target) {
        if (target.getBuilding() != null) {
            target.setBuilding(null);
        }

        if (selectedDirections == null || selectedDirections.length == 0) {
            return;
        }

        Road road = new Road(selectedDirections, "Continue");
        road.setIntersection(SetRoute.isIntersection());
        target.setRoad(road);

        selectedDirections = null;
    }

    public static void setIntersectionFlag(boolean flag) {intersectionFlag = flag;}
    public static boolean isIntersection() {return intersectionFlag;}

    @Override
    public String toString() {
        return "route";
    }
}