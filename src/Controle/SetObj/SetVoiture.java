package Controle.SetObj;

import Controle.Controle;
import Modele.Case;
import Modele.CityMap;
import Modele.Position;
import Modele.Stack2;
import Modele.Vehicle.Car;
import Modele.Vehicle.Vehicle;
import Vue.TrafficSimulatorGUI;
import Modele.Direction;



import java.util.Random;


public class SetVoiture implements Update {
    
    public static final SetVoiture set = new SetVoiture();

    public void update(CityMap map, Stack2 c){
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        if (toChange.getVehicle() == null){
            if (toChange.getRoad() != null){
                Random rand = new Random();

                Position destination = map.getAccessiblePosition().get(rand.nextInt(map.getAccessiblePosition().size()));
                Car voiture = new Car("Car" + Vehicle.nbCar, 2, c.getA(), map, destination);
                System.out.println(CityMap.getCase(map.getGrid(), c.getA()));
                voiture.checkPath();
                Controle.addUpdateCar(c.getA());
                Controle.addUpdate();
                TrafficSimulatorGUI.setUpdate(true);
            }
        };
        
    }

    @Override
    public String toString(){
        return "voiture";
    }
}