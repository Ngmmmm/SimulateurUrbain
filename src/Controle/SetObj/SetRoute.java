package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Vue.TrafficSimulatorGUI;

public class SetRoute implements Update {
    
    public static final SetRoute set = new SetRoute();
    private static Direction selectedDirection = null; // Direction sélectionnée via le panneau

    public static void setSelectedDirection(Direction direction) {
        selectedDirection = direction;
    }

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats) {
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        
        if (toChange.getRoad() != null) {
            if (selectedDirection != null) {
                // Si une direction a été sélectionnée via le panneau
                Direction[] newDir = new Direction[]{selectedDirection};
                toChange.getRoad().setDirection(newDir);
                selectedDirection = null; // Réinitialiser après utilisation
            } else {
                // Rotation cyclique comme avant
                Direction[] newDir = new Direction[toChange.getRoad().getDirection().length];
                for(int i = 0; i < toChange.getRoad().getDirection().length; i++) {
                    newDir[i] = switch(toChange.getRoad().getDirection()[i].toString()) {
                        case "UP" -> Direction.RIGHT;
                        case "RIGHT" -> Direction.DOWN;
                        case "DOWN" -> Direction.LEFT;
                        case "LEFT" -> Direction.UP;
                        default -> Direction.UP;
                    };
                }
                toChange.getRoad().setDirection(newDir);
            }
        } else {
            // Création d'une nouvelle route
            if(toChange.getBuilding() != null) {
                toChange.setBuilding(null);
            }
            
            Direction[] dir = new Direction[1];
            dir[0] = (selectedDirection != null) ? selectedDirection : Direction.UP;
            toChange.setRoad(new Road(dir, "Continue"));
            selectedDirection = null; // Réinitialiser après utilisation
        }
        
        Controle.addUpdate();
        TrafficSimulatorGUI.setUpdate(true);
    }

    @Override
    public String toString() {
        return "route";
    }
}