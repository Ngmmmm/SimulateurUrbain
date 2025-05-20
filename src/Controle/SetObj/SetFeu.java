package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.*;
import Vue.TrafficSimulatorGUI;
import java.util.ArrayList;


/**
 * La SetFeu implémente l'interface Update et contient les fonctions d'ajout et modifications de feu de signalisation.
 * 
 * 
 * <p> 
 * Cette classe contient des méthodes pour :
 * </p>
 * 
 * <ul>
 * <li> Appliquer les changements liés aux feux en fonction des cases cliquées {@link #update(CityMap, Stack2, Statistique)}. </li>
 * <li> Créer un nouveau feu en fonction des spécifications de la case cliquée {@link #createNewTrafficLight(Case, Stack2, CityMap)} </li>
 * <li> Regrouper les feux des deux groupes associées aux cases cliquées en un seul {@link #mergeTrafficLightGroups(TrafficLight, TrafficLight)}</li>
 * <li> Modifier la couleur des feux du groupe de la case cliqué en la couleur opposé {@link #toggleTrafficLightColor(TrafficLightGroup)}</li>
 * 
 * </ul>
 */

public class SetFeu implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetFeu set;

    public SetFeu(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    /**
     * Fonction principal d'ajout et changement de feu qui selon le cas va appliquer la sous-fonction appropriée.
     * 
     * @param map carte sur laquel placer/changer les feux
     * @param c pile contenant les deux dernières cases cliquées et contenant les informations des cases
     * @param stats objet contenant les informations liées au statistique pour le mettre à jour
     * 
     */
    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case caseA = CityMap.getCase(map.getGrid(), c.getA());

        // On ne modifie rien si un bâtiment est présent
        if (caseA.getBuilding() != null) return;

        Signalling signallingA = caseA.getSignalling();

        if (!(signallingA instanceof TrafficLight trafficLightA)) {
            createNewTrafficLight(caseA, map);
        } else{
            if (c.getB() == null) {
                toggleTrafficLightColor(trafficLightA.getTrafficLightGroup(ctrl));
            }else {
                Case caseB = CityMap.getCase(map.getGrid(), c.getB());
                Signalling signallingB = caseB.getSignalling();

                if (signallingB instanceof TrafficLight trafficLightB) {
                    if (trafficLightA.getIDGroupe() == trafficLightB.getIDGroupe()) return;
                    mergeTrafficLightGroups(trafficLightA, trafficLightB);

                } else {
                    toggleTrafficLightColor(trafficLightA.getTrafficLightGroup(ctrl));
                }
            }
        }

        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    
    /** 
     * Fonction créant un nouveau TrafficLight et l'ajoutant à la carte
     * @param caseA Case sur laquel placer le nouveau feu
     * @param map Carte sur laquel placer le nouveau feu
     */
    private void createNewTrafficLight(Case caseA, CityMap map) {
        Direction direction = caseA.getRoad().getDirection()[0];
        TrafficLight newLight = new TrafficLight(caseA.getPos(), direction, map);
        caseA.setSignalling(newLight);

        ArrayList<TrafficLight> group = new ArrayList<>();
        group.add(newLight);
        new TrafficLightGroup(group, ctrl);
    }

    
    /** 
     * Fonction regroupant les TrafficLights des deux groupes des TrafficLights passés en argument
     * @param a TrafficLight dont le groupe va servir de récipient pour les trafficLights du groupe de b
     * @param b TrafficLight dont le groupe va être déplacer dans le groupe de a
     */
    private void mergeTrafficLightGroups(TrafficLight a, TrafficLight b) {
        TrafficLightGroup groupA = null;
        TrafficLightGroup groupB = null;
        for (TrafficLightGroup g : ctrl.getLights()) {
            if (g.getId() == a.getIDGroupe()) groupA = g;
            if (g.getId() == b.getIDGroupe()) groupB = g;
            if (groupA != null && groupB != null) break;
        }

        if (groupA != null && groupB != null && groupA != groupB) {
            ctrl.removeLights(b);
            groupA.merge(groupB);
            groupA.stop();
        }
    }

    
    /** 
     * Fonction modifiant en la couleur opposé tout les feux d'un groupe donné
     * @param groupe groupe allant changer de couleur
     */
    private void toggleTrafficLightColor(TrafficLightGroup groupe) {
        for(TrafficLight light : groupe.getLights()){
            light.setColor(
                switch (light.getColor()) {
                    case "GREEN" -> LightColor.RED;
                    case "RED" -> LightColor.GREEN;
                    case "ORANGE" -> LightColor.ORANGE;
                    default -> LightColor.RED;
                },
                ctrl
            );
        }
        
    }

    @Override
    public String toString() {
        return "feu";
    }
}
