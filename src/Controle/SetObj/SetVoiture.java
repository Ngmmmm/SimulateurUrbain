package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Vehicle.*;
import Vue.TrafficSimulatorGUI;
import java.util.List;
import java.util.Random;

public class SetVoiture implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;
    private final Random random = new Random();

    public static SetVoiture set;

    public SetVoiture(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case target = CityMap.getCase(map.getGrid(), c.getA());
        Case targetB = (c.getB() == null)? null : CityMap.getCase(map.getGrid(), c.getB());
        if (target.getRoad() != null){
            if(target.getVehicle() == null){
                if (targetB != null && targetB.getVehicle() == null && targetB.getRoad() != null){
                    Car car = new Car("Car" + Vehicle.nbCar, 0, c.getB(), map, c.getA(), stats, ctrl);
                    car.checkPath();
                    ctrl.addCar(c.getB());
                }else{
                    spawnCarIfPossible(map, c.getA(), stats);
                }
            }
        }else if(targetB != null && targetB.getVehicle() == null && targetB.getRoad() != null){
            spawnCarIfPossible(map, c.getB(), stats);
        }
    }

    private void spawnCarIfPossible(CityMap map, Position p, Statistique stats) {
        List<Position> accessible = map.getAccessiblePosition();
        if (accessible.isEmpty()) return;

        Position destination = accessible.get(random.nextInt(accessible.size()));
        Car car = new Car("Car" + Vehicle.nbCar, 0, p, map, destination, stats, ctrl);

        car.checkPath();

        ctrl.addCar(p);
    }

    @Override
    public String toString() {
        return "voiture";
    }
}
