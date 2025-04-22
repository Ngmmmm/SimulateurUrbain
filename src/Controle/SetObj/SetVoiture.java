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

        if (target.getVehicle() == null && target.getRoad() != null) {
            spawnCarIfPossible(map, c, stats, target);
        }
    }

    private void spawnCarIfPossible(CityMap map, Stack2 c, Statistique stats, Case target) {
        List<Position> accessible = map.getAccessiblePosition();
        if (accessible.isEmpty()) return;

        Position destination = accessible.get(random.nextInt(accessible.size()));
        Car car = new Car("Car" + Vehicle.nbCar, 2, c.getA(), map, destination, stats, ctrl);

        car.checkPath();

        ctrl.addUpdateCar(c.getA());
        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    @Override
    public String toString() {
        return "voiture";
    }
}
