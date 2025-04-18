package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.*;
import Vue.TrafficSimulatorGUI;
import java.util.ArrayList;

public class SetFeu implements Update {
    
    public static final SetFeu set = new SetFeu();

    @Override
    public void update(CityMap map, Stack2 c, Statistique stats){
        Case toChangeA = CityMap.getCase(map.getGrid(), c.getA());
        if(toChangeA.getBuilding() != null){
            return ;
        }
        if (toChangeA.getSignalling() == null || !(toChangeA.getSignalling() instanceof TrafficLight)){
            toChangeA.setSignalling(new TrafficLight(c.getA(), toChangeA.getRoad().getDirection()[0], map));
            ArrayList<TrafficLight> group = new ArrayList<>();
            group.add((TrafficLight) toChangeA.getSignalling());
            new TrafficLightGroup(group);
        }else{
            Case toChangeB = CityMap.getCase(map.getGrid(), c.getB());
            if (toChangeB.getSignalling()!= null && toChangeB.getSignalling() instanceof TrafficLight){
                if (((TrafficLight) toChangeA.getSignalling()).getIDGroupe() == 
                    ((TrafficLight) toChangeB.getSignalling()).getIDGroupe()){
                        return ;
                }
                TrafficLightGroup a = null;
                TrafficLightGroup b = null;
                for(TrafficLightGroup g : Controle.lights){
                    if (g.getId() == ((TrafficLight) toChangeA.getSignalling()).getIDGroupe()){
                        a = g;
                    }
                    if (g.getId() == ((TrafficLight) toChangeB.getSignalling()).getIDGroupe()){
                        b = g;
                    }
                    if (b != null && a != null){
                        break;
                    }
                }
                Controle.removeLight((TrafficLight) toChangeB.getSignalling());
                a.merge(b);
            }else{
                ((TrafficLight) toChangeA.getSignalling()).setColor(
                    switch (((TrafficLight) toChangeA.getSignalling()).getColor()) {
                        case "GREEN" ->  LightColor.RED;
                        case "RED" -> LightColor.GREEN;
                        case "ORANGE" -> LightColor.ORANGE; // L'orange reste orange
                        default -> LightColor.RED;
                    }
                );
            }
        }
        Controle.addUpdate();
        TrafficSimulatorGUI.setUpdate(true);

    }
    
    @Override
    public String toString(){
        return "feu";
    }
}
