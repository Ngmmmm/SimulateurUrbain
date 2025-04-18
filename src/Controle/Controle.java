package Controle;

import Controle.SetObj.*;
import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.*;
import Vue.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Controle {



    Simulation s;
    Statistique stats;
    CityMap cityMap; 
    Direction dir;
    Car car;
    ArrayList<TrafficLight> allLights; 
    TrafficLight feu;
    TrafficLightGroup groupe1; 

    static Timer ctrlTimer;
    static Timer t2;
    public static int speed = 10;

    static boolean needUpdate = false;
    public static boolean timerIsRunning = false;
    public static ArrayList<TrafficLightGroup> lights = new ArrayList<>();
    static ArrayList<Position> toUpdateCar = new ArrayList<>();
    public static Update nextChange;
    public static Stack2 caseUpdate = new Stack2();

    Controle(){
        initSim();
        ctrlTimer = new Timer(50*speed, createActionListener());
        t2 = new Timer(50*speed, (ActionEvent e) -> {
            if(!TrafficSimulatorGUI.getPause() != ctrlTimer.isRunning()){
                if (!TrafficSimulatorGUI.getPause()) {
                    nextChange = null;
                    caseUpdate.clear();
                    start();
                    
                }
                else {
                    pause();
                }
            }
            if(TrafficSimulatorGUI.getPause() && !caseUpdate.isEmpty() && nextChange != null){
                nextChange.update(cityMap, caseUpdate, stats);
                caseUpdate.clear();
                nextChange = null;
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
        stats = new Statistique();
        s = new Simulation(stats);
        cityMap = s.getCityMap();
    }

    public static void removeUpdateCar(Position p){
        toUpdateCar.removeIf(p::equals);
    }

    public static void removeLight(TrafficLight light){
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

    public static void setChange(String s) {
        if (s.startsWith("route:")) {
            String direction = s.split(":")[1];
            SetRoute.setSelectedDirection(Direction.valueOf(direction));
            nextChange = SetRoute.set;
        } else {
            nextChange = switch (s) {
                case "route" -> SetRoute.set; 
                case "feu" -> SetFeu.set;
                case "maison" -> SetMaison.set;
                case "voiture" -> SetVoiture.set;
                default -> null;
            };
        }
    }


    public ActionListener createActionListener(){
        return (ActionEvent e) -> {
            Vehicle tmpCar;
            ArrayList<Position> iter = new ArrayList<>();
            for(Position p : toUpdateCar){
                iter.add(p);
            }
            toUpdateCar.clear();
            for(Position p : iter){
                tmpCar = CityMap.getCase(cityMap.getGrid(), p).getVehicle();
                needUpdate |=tmpCar.followPath(tmpCar.path, stats);
            }
            if (needUpdate){
                TrafficSimulatorGUI.setUpdate(true);
            }
        };
    }

    public static void modifySpeed(){
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



    public static void main(String[] args) {

        Controle a = new Controle(); 

        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> {
                new TrafficSimulatorGUI(a.cityMap, a.stats);
            }
        );

    }
}


