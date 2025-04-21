package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.TrafficLight;
import Vue.TrafficSimulatorGUI;

public class SetMaison implements Update {
    
    Controle ctrl;
    TrafficSimulatorGUI gui;

    public SetMaison(Controle ctrl, TrafficSimulatorGUI gui){
        this.ctrl = ctrl;
        this.gui = gui;
    }

    public static SetMaison set;

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats){
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        if(toChange.getVehicle()!= null){
            ctrl.removeUpdateCar(c.getA());
            toChange.setVehicle(null);
        }
        toChange.setRoad(null);
        if(toChange.getSignalling() != null && toChange.getSignalling() instanceof TrafficLight){
            ctrl.removeLight((TrafficLight) toChange.getSignalling());
        }
        toChange.setSignalling(null);
        toChange.setBuilding(new Building());

        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    @Override
    public String toString(){
        return "maison";
    }
}