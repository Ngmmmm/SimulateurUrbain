package Controle.SetObj;

import Modele.Stack2;
import Modele.Statistique;
import Vue.TrafficSimulatorGUI;
import Modele.Position;
import Modele.Road;
import Modele.CityMap;
import Modele.Direction;
import Modele.Case;

import Controle.Controle;

public class SetRoute implements Update {
    
    public static final SetRoute set = new SetRoute();

    public void update(CityMap map, Stack2 c, Statistique stats){
        Case toChange = CityMap.getCase(map.getGrid(), c.getA());
        if (toChange.getRoad() != null){
            Direction[] newDir = new Direction[toChange.getRoad().getDirection().length];
            for(int i = 0; i<toChange.getRoad().getDirection().length; i++){
                newDir[i] = switch(toChange.getRoad().getDirection()[i].toString()){
                    case "UP" -> Direction.RIGHT;
                    case "RIGHT" -> Direction.DOWN;
                    case "DOWN" -> Direction.LEFT;
                    case "LEFT" -> Direction.UP;
                    default -> Direction.UP;
                };
            }
            toChange.getRoad().setDirection(newDir);
        }else{
            if(toChange.getBuilding() != null){
                toChange.setBuilding(null);
            }
            Direction[] dir = new Direction[1];
            dir[0] = Direction.UP;
            toChange.setRoad(new Road(dir, "Continue"));
        }
        Controle.addUpdate();
        TrafficSimulatorGUI.setUpdate(true);

    }

    @Override
    public String toString(){
        return "route";
    }
}
