package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Vue.TrafficSimulatorGUI;

public class SetRoute implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetRoute set;
    private static Direction selectedDirection = null;

    public SetRoute(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    public static void setSelectedDirection(Direction direction) {
        selectedDirection = direction;
    }

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case target = CityMap.getCase(map.getGrid(), c.getA());

        if (target.getRoad() != null) {
            updateExistingRoad(target);
        } else {
            createNewRoad(target);
        }

        ctrl.addUpdate();
        gui.setUpdate(true);
    }

    private void updateExistingRoad(Case target) {
        if (selectedDirection != null) {
            target.getRoad().setDirection(new Direction[]{selectedDirection});
            selectedDirection = null;
        } else {
            Direction[] rotated = rotateDirections(target.getRoad().getDirection());
            target.getRoad().setDirection(rotated);
        }
    }

    private void createNewRoad(Case target) {
        if (target.getBuilding() != null) {
            target.setBuilding(null);
        }

        Direction[] dir = new Direction[]{selectedDirection != null ? selectedDirection : Direction.UP};
        target.setRoad(new Road(dir, "Continue"));

        selectedDirection = null;
    }

    private Direction[] rotateDirections(Direction[] original) {
        Direction[] rotated = new Direction[original.length];
        for (int i = 0; i < original.length; i++) {
            rotated[i] = switch (original[i]) {
                case UP -> Direction.RIGHT;
                case RIGHT -> Direction.DOWN;
                case DOWN -> Direction.LEFT;
                case LEFT -> Direction.UP;
                default -> Direction.UP;
            };
        }
        return rotated;
    }

    @Override
    public String toString() {
        return "route";
    }
}
