package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.TrafficLight;
import Vue.TrafficSimulatorGUI;

public class SetMaison implements Update {
    
    public static final SetMaison set = new SetMaison();

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats){
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        if(toChange.getVehicle()!= null){
            Controle.removeUpdateCar(c.getA());
            toChange.setVehicle(null);
        }
        toChange.setRoad(null);
        if(toChange.getSignalling() != null && toChange.getSignalling() instanceof TrafficLight){
            Controle.removeLight((TrafficLight) toChange.getSignalling());
        }
        toChange.setSignalling(null);
        toChange.setBuilding(new Building());

        Controle.addUpdate();
        TrafficSimulatorGUI.setUpdate(true);
    }

    @Override
    public String toString(){
        return "maison";
    }
}