package Controle.SetObj;

import Controle.Controle;
import Modele.Case;
import Modele.CityMap;
import Modele.Position;
import Modele.Stack2;
import Modele.Statistique;
import Modele.Vehicle.Car;
import Modele.Vehicle.Vehicle;
import Vue.TrafficSimulatorGUI;
import java.util.Random;

public class SetVoiture implements Update {
    
    public static final SetVoiture set = new SetVoiture();

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats){
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        if (toChange.getVehicle() == null){
            if (toChange.getRoad() != null){
                Random rand = new Random();

                Position destination = map.getAccessiblePosition().get(rand.nextInt(map.getAccessiblePosition().size()));
                Car voiture = new Car("Car" + Vehicle.nbCar, 2, c.getA(), map, destination, stats);
                voiture.checkPath();
                Controle.addUpdateCar(c.getA());
                Controle.addUpdate();
                TrafficSimulatorGUI.setUpdate(true);
            }
        }
    }

    @Override
    public String toString(){
        return "voiture";
    }
}