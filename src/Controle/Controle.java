package Controle;

import java.util.ArrayList;
import java.util.LinkedList;

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

    Timer ctrlTimer;
    Timer t2;

    static boolean needUpdate = false;
    public static boolean timerIsRunning = false;
    static ArrayList<Position> toUpdateCar = new ArrayList<Position>();

    Controle(){
        initSim();
        ctrlTimer = new Timer(1000, createActionListener());
        t2 = new Timer(500, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(timerIsRunning != ctrlTimer.isRunning()){
                    System.out.println(timerIsRunning != ctrlTimer.isRunning());
                    if (timerIsRunning) {
                        start();
                    }
                    else {
                        pause();
                    }
                }
                System.out.println(ctrlTimer.isRunning());
            }
        });

        start();
        t2.start();
    }

    public static boolean getUpdate(){
        return needUpdate;
    }

    public static void addUpdate(){
        needUpdate |= true;
    }

    public static void addUpdateCar(Position pos){
        toUpdateCar.add(pos);
        addUpdate();
    }

    public void initSim(){
        s = new Simulation();
        cityMap = s.getCityMap();
    }

    public void pause(){
        ctrlTimer.stop();
        timerIsRunning = false;
    }

    public void start(){
        ctrlTimer.start();
        timerIsRunning = true;
    }



    public ActionListener createActionListener(){
        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Vehicle tmpCar;
                ArrayList<Position> iter = new ArrayList<Position>();
                for(Position p : toUpdateCar){
                    iter.add(p);
                }
                toUpdateCar.clear();
                for(Position p : iter){
                    tmpCar = CityMap.getCase(cityMap.getGrid(), p).getVehicle();
                    needUpdate |=tmpCar.followPath(tmpCar.path);
                }
                if (needUpdate){
                    TrafficSimulatorGUI.setUpdate(true);
                }
            }
        };
    }



    public static void main(String[] args) {

        Controle a = new Controle(); 

        


        

        LinkedList<Position> pos = Pathfinding.findShortestPath(a.cityMap, new Position(0,2), new Position(45,41) );
        for(int i = 0; i < pos.size()  ; i++){
            System.out.println(pos.get(i));

        }
        // System.out.println(car.getPosition().getX() +"," + car.getPosition().getY());
        // car.suivreCheminComplet();
        // System.out.println(car.getPosition().getX() +"," + car.getPosition().getY());
        
        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> {
                new TrafficSimulatorGUI(a.cityMap);
            }
        );


        LinkedList<Position> res = Pathfinding.FindSpawnPosition(a.cityMap);
        System.out.println("#########################");
        for(int i = 0; i < res.size()  ; i++){
            System.out.println(res.get(i));

        }
        System.out.println("#########################");
        
    }
}
