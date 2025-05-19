package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.*;
import Vue.TrafficSimulatorGUI;
import java.util.ArrayList;

public class SetFeu implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetFeu set;

    public SetFeu(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case caseA = CityMap.getCase(map.getGrid(), c.getA());

        // On ne modifie rien si un bâtiment est présent
        if (caseA.getBuilding() != null) return;

        Signalling signallingA = caseA.getSignalling();

        if (!(signallingA instanceof TrafficLight trafficLightA)) {
            createNewTrafficLight(caseA, c, map);
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

    private void createNewTrafficLight(Case caseA, Stack2 c, CityMap map) {
        Direction direction = caseA.getRoad().getDirection()[0];
        TrafficLight newLight = new TrafficLight(c.getA(), direction, map);
        caseA.setSignalling(newLight);

        ArrayList<TrafficLight> group = new ArrayList<>();
        group.add(newLight);
        new TrafficLightGroup(group, ctrl);
    }

    private void mergeTrafficLightGroups(TrafficLight a, TrafficLight b) {
        TrafficLightGroup groupA = null;
        TrafficLightGroup groupB = null;
        for (TrafficLightGroup g : ctrl.getLights()) {
            if (g.getId() == a.getIDGroupe()) groupA = g;
            if (g.getId() == b.getIDGroupe()) groupB = g;
            if (groupA != null && groupB != null) break;
        }

        if (groupA != null && groupB != null && groupA != groupB) {
            ctrl.removeLight(b);
            groupA.merge(groupB);
            groupA.stop();
        }
    }

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
