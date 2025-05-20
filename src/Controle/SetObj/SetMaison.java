package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.TrafficLight;
import Vue.TrafficSimulatorGUI;


/**
 * La SetMaison implémente l'interface Update et contient les fonctions d'ajout de Building
 * 
 * 
 * <p> 
 * Cette classe contient des méthodes pour :
 * </p>
 * 
 * <ul>
 * <li> Appliquer les changements liés aux Building {@link #update(CityMap, Stack2, Statistique)}. </li>
 * <li> Retirer les voitures d'une Case {@link #removeVehicleIfPresent(Case)}. </li>
 * <li> Retirer les signalisations d'une Case {@link #clearRoadAndSignalling(Case)}</li>
 * </ul>
 */

public class SetMaison implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetMaison set;

    public SetMaison(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    
    /** 
     * Fonction qui va changer une case avec un Road en Building
     * @param map carte sur laquel ajouter la maison
     * @param c pile contenant la case à modifier et ses informations
     * @param stats objet contenant les informations liées au statistique pour le mettre à jour
     */
    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case target = CityMap.getCase(map.getGrid(), c.getA());

        removeVehicleIfPresent(target);
        clearRoadAndSignalling(target);
        target.setBuilding(new Building());

        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    
    /** 
     * Fonction qui retire une voiture d'une case si présente
     * @param target Case dont la voiture doit être enlever
     */
    private void removeVehicleIfPresent(Case target) {
        if (target.getVehicle() != null) {
            ctrl.removeUpdateCar(target.getPos());
            target.setVehicle(null);
        }
    }

    
    /** 
     * Fonction qui retire les signalisations d'une case si présente
     * @param target Case dont les signalisations doivent être enlever
     */
    private void clearRoadAndSignalling(Case target) {
        target.setRoad(null);

        if (target.getSignalling() instanceof TrafficLight trafficLight) {
            ctrl.removeLight(trafficLight);
        }

        target.setSignalling(null);
    }

    @Override
    public String toString() {
        return "maison";
    }
}
