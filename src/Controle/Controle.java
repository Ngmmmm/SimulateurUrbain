package Controle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Controle.SetObj.*;
import Modele.Signalling.*;
import Modele.Vehicle.*;
import Modele.*;
import Vue.*;


import java.awt.event.*;

public class Controle {



    Simulation s;
    TrafficSimulatorGUI gui;
    Statistique stats;
    CityMap cityMap; 
    Direction dir;
    Car car;
    ArrayList<TrafficLight> allLights; 
    TrafficLight feu;
    TrafficLightGroup groupe1; 

    Timer ctrlTimer;
    Timer t2;
    int speed = 10;

    boolean needUpdate = false;
    boolean timerIsRunning = false;
    ArrayList<TrafficLightGroup> lights = new ArrayList<TrafficLightGroup>();
    ArrayList<Position> toUpdateCar = new ArrayList<Position>();
    Update nextChange;
    Stack2 caseUpdate = new Stack2();

    Controle(){
        initSim();
        ctrlTimer = new Timer(50*speed, createActionListener());
        t2 = new Timer(50*speed, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(gui.isRunning() != ctrlTimer.isRunning()){
                    if (gui.isRunning()) {
                        nextChange = null;
                        caseUpdate.clear();
                        start();
                        
                    }
                    else {
                        pause();
                    }
                }
                if(!gui.isRunning() && !caseUpdate.isEmpty() && nextChange != null){
                    nextChange.update(cityMap, caseUpdate, stats);
                    caseUpdate.clear();
                    nextChange = null;
                }
            }
        });
        gui = new TrafficSimulatorGUI(cityMap, stats, this);
        initSet();
        start();
        t2.start();
    }

    public boolean getUpdate(){
        return needUpdate;
    }


    public void addUpdate(){
        needUpdate |= true;
    }

    public void addUpdateCar(Position pos){
        toUpdateCar.add(pos);
        addUpdate();
    }

    public Stack2 getCaseUpdate(){
        return caseUpdate;
    }

    public ArrayList<TrafficLightGroup> getLights(){
        return lights;
    }

    public void initSim(){
        stats = new Statistique();
        s = new Simulation(stats, this);
        cityMap = s.getCityMap();
    }

    public void removeUpdateCar(Position p){
        toUpdateCar.removeIf(new Predicate<Position>(){
            public boolean test(Position pos){
                return p.equals(pos);
            }
        });
    }

    public void removeLight(TrafficLight light){
        for(TrafficLightGroup t : lights){
            if (t.getId() == light.getIDGroupe()){
                t.remove(light);
            }
        }
    }

    public void pause(){
        ctrlTimer.stop();
        for (TrafficLightGroup g : lights){
            if (!g.isDown()) g.stop();
        }
    }

    public void start(){
        ctrlTimer.start();
        for(TrafficLightGroup g : lights){
            if (g.isDown()) g.startCycle();
        }
    }

    public void setChange(String s){
        nextChange = switch (s){
            case "route" -> SetRoute.set; 
            case "feu" -> SetFeu.set;
            case "maison" -> SetMaison.set;
            case "voiture" -> SetVoiture.set;
            default -> null;
        };
    }

    public int getSpeed(){
        return speed;
    }

    public void setSpeed(int n){
        speed = n;
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
                    needUpdate |=tmpCar.followPath(tmpCar.path, stats);
                }
                if (needUpdate){
                    gui.setUpdate(true);
                }
            }
        };
    }

    public void modifySpeed(){
        ctrlTimer.setDelay(speed*10);
        t2.setDelay(speed*10);
        if (ctrlTimer.isRunning() && t2.isRunning()){
            ctrlTimer.stop();  // Arrêter le timer en cours
            t2.stop();
        }
        ctrlTimer.setDelay(50*speed); // Modifier l'intervalle
        t2.setDelay(50*speed);
        ctrlTimer.start(); // Redémarrer avec le nouveau délai
        t2.start();
        for(int i = 0; i< lights.size();i++){
            lights.get(i).reset();
        }
    }   

    public void initSet(){
        SetFeu.set = new SetFeu(this, gui);
        SetMaison.set = new SetMaison(this, gui);
        SetRoute.set = new SetRoute(this, gui);
        SetVoiture.set = new SetVoiture(this, gui);
    }



    public static void main(String[] args) {



        
        // System.out.println(car.getPosition().getX() +"," + car.getPosition().getY());
        // car.suivreCheminComplet();
        // System.out.println(car.getPosition().getX() +"," + car.getPosition().getY());
        
        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> {
                new Controle();
            }
        );

    }
}


