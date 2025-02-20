package Controle;

import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Modele.Signalling.*;
import Modele.Vehicle.*;
import Modele.*;
import Vue.*;


import java.awt.event.*;

public class Controle {



    Simulation s;
    CityMap cityMap; 
    Direction dir;
    Car car;
    ArrayList<TrafficLight> allLights; 
    TrafficLight feu;
    TrafficLightGroup groupe1; 

    static ArrayList<TuplePosAct> toUpdate = new ArrayList<TuplePosAct>();

    Controle(){
        initSim();

    }

    public static void addUpdate(TuplePosAct change){
        toUpdate.add(change);
    }

    public void initSim(){
        s = new Simulation();
        cityMap = s.getCityMap();
    }
    public static void main(String[] args) {

        Controle a = new Controle(); 

        



        

        ArrayList<Position> pos = Pathfinding.findShortestPath(a.cityMap, new Position(0,2), new Position(45,41) );
        for(int i = 0; i < pos.size()  ; i++){
            System.out.println(pos.get(i));

        }
        // System.out.println(car.getPosition().getX() +"," + car.getPosition().getY());
        // car.suivreCheminComplet();
        // System.out.println(car.getPosition().getX() +"," + car.getPosition().getY());
        
        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> {
            TrafficSimulatorGUI gui = new TrafficSimulatorGUI(a.cityMap);

            ActionListener update = new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    if(!toUpdate.isEmpty()){
                        for(TuplePosAct obj : toUpdate){
                            if (obj.getAct() != null) {
                                obj.getAct().update(a.cityMap.getGrid()[obj.getX()][obj.getY()]);
                            }
                        }
                        gui.repaint();
                        toUpdate.clear();
                    }
                }
            };

            new Timer(500, update).start();


            }
        );


        ArrayList<Position> res = Pathfinding.FindSpawnPosition(a.cityMap);
        System.out.println("#########################");
        for(int i = 0; i < res.size()  ; i++){
            System.out.println(res.get(i));

        }
        System.out.println("#########################");
        
    }
}
