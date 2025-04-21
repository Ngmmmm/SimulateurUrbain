package Controle.SetObj;

import Controle.Controle;
import Modele.*;
import Modele.Signalling.*;
import Vue.TrafficSimulatorGUI;
import java.util.ArrayList;

public class SetFeu implements Update {
    
    Controle ctrl;
    TrafficSimulatorGUI gui;

    public SetFeu(Controle ctrl, TrafficSimulatorGUI gui){
        this.ctrl = ctrl;
    }

    public static SetFeu set;

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
            new TrafficLightGroup(group, ctrl);
        }else{
            Case toChangeB = CityMap.getCase(map.getGrid(), c.getB());
            if (toChangeB.getSignalling()!= null && toChangeB.getSignalling() instanceof TrafficLight){
                if (((TrafficLight) toChangeA.getSignalling()).getIDGroupe() == 
                    ((TrafficLight) toChangeB.getSignalling()).getIDGroupe()){
                        return ;
                }
                TrafficLightGroup a = null;
                TrafficLightGroup b = null;
                for(TrafficLightGroup g : ctrl.getLights()){
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
                ctrl.removeLight((TrafficLight) toChangeB.getSignalling());
                a.merge(b);
            }else{
                ((TrafficLight) toChangeA.getSignalling()).setColor(
                    switch (((TrafficLight) toChangeA.getSignalling()).getColor()) {
                        case "GREEN" ->  LightColor.RED;
                        case "RED" -> LightColor.GREEN;
                        case "ORANGE" -> LightColor.ORANGE; // L'orange reste orange
                        default -> LightColor.RED;
                    }, ctrl
                );
            }
        }
        ctrl.addUpdate();
        gui.setUpdate(true);

    }
    
    @Override
    public String toString(){
        return "feu";
    }
}
