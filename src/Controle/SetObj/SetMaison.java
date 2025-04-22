package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.TrafficLight;
import Vue.TrafficSimulatorGUI;

public class SetMaison implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetMaison set;

    public SetMaison(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case target = CityMap.getCase(map.getGrid(), c.getA());

        removeVehicleIfPresent(target, c);
        clearRoadAndSignalling(target);
        target.setBuilding(new Building());

        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    private void removeVehicleIfPresent(Case target, Stack2 c) {
        if (target.getVehicle() != null) {
            ctrl.removeUpdateCar(c.getA());
            target.setVehicle(null);
        }
    }

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
