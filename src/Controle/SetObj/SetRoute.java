package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Vue.TrafficSimulatorGUI;

public class SetRoute implements Update {

    private final Controle ctrl;
    private final TrafficSimulatorGUI gui;

    public static SetRoute set;
    private static Direction[] selectedDirections = null;
    private static boolean intersectionFlag = false;

    public SetRoute(Controle ctrl, TrafficSimulatorGUI gui) {
        this.ctrl = ctrl;
        this.gui = gui;
    }

    public static void setSelectedDirections(Direction[] directions) {
        selectedDirections = directions;
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
        if (selectedDirections != null && selectedDirections.length > 0) {
            target.getRoad().setDirection(selectedDirections);
            target.getRoad().setIntersection(SetRoute.isIntersection());
            selectedDirections = null;
        }
        
    }

    private void createNewRoad(Case target) {
        if (target.getBuilding() != null) {
            target.setBuilding(null);
        }

        if (selectedDirections == null || selectedDirections.length == 0) {
            return;
        }

        Road road = new Road(selectedDirections, "Continue");
        road.setIntersection(SetRoute.isIntersection());
        target.setRoad(road);

        selectedDirections = null;
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

    public static void setIntersectionFlag(boolean flag) {intersectionFlag = flag;}
    public static boolean isIntersection() {return intersectionFlag;}

    @Override
    public String toString() {
        return "route";
    }
}