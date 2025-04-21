package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Vehicle.*;
import Vue.TrafficSimulatorGUI;
import java.util.Random;

public class SetVoiture implements Update {
    
    Controle ctrl;
    TrafficSimulatorGUI gui;

    public SetVoiture(Controle ctrl, TrafficSimulatorGUI gui){
        this.ctrl = ctrl;
        this.gui = gui;
    }

    public static SetVoiture set;

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats){
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        if (toChange.getVehicle() == null){
            if (toChange.getRoad() != null){
                Random rand = new Random();

                Position destination = map.getAccessiblePosition().get(rand.nextInt(map.getAccessiblePosition().size()));
                Car voiture = new Car("Car" + Vehicle.nbCar, 2, c.getA(), map, destination, stats, ctrl);
                voiture.checkPath();
                ctrl.addUpdateCar(c.getA());
                ctrl.addUpdate();
                gui.setUpdate(true);
            }
        }
    }

    @Override
    public String toString(){
        return "voiture";
    }
}