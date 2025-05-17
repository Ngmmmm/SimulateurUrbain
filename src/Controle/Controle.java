package Controle;

import Controle.SetObj.*;
import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.*;
import Vue.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Controle {

    // --- Simulation et interface ---
    private Simulation s;
    private TrafficSimulatorGUI gui;
    private Statistique stats;
    private CityMap cityMap;

    // --- Timers ---
    private Timer ctrlTimer;
    private Timer t2;
    private int speed = 10;

    // --- États de la simulation ---
    private boolean needUpdate = false;
    private boolean timerIsRunning = false;

    // --- Objets de contrôle ---
    private ArrayList<TrafficLightGroup> lights = new ArrayList<>();
    private ArrayList<Position> toUpdateCar = new ArrayList<>();
    private Update nextChange;
    private final Stack2 caseUpdate = new Stack2();
    private ArrayList<Position> iter;

    // --- Constructeur ---
    public Controle() {
        initSim();
        ctrlTimer = new Timer(50 * speed, createActionListener());
        t2 = new Timer(50 * speed, createGlobalListener());
        gui = new TrafficSimulatorGUI(cityMap, stats, this);
        initSet();
        start();
        t2.start();
    }

    // --- Initialisation ---
    private void initSim() {
        stats = new Statistique();
        s = new Simulation(stats, this);
        cityMap = s.getCityMap();
    }

    private void initSet() {
        SetFeu.set = new SetFeu(this, gui);
        SetMaison.set = new SetMaison(this, gui);
        SetRoute.set = new SetRoute(this, gui);
        SetVoiture.set = new SetVoiture(this, gui);
    }

    // --- Timer / Simulation ---
    public void start() {
        ctrlTimer.start();
        timerIsRunning = true;
        for (TrafficLightGroup g : lights){
            if (g.isDown()) g.startCycle();
        }
    }

    public void pause() {
        ctrlTimer.stop();
        timerIsRunning = false;
        for (TrafficLightGroup g : lights){
            if (!g.isDown()) {g.stop();}
        }
    }

    public void modifySpeed() {
        int newDelay = 50 * speed;
        ctrlTimer.setDelay(newDelay);
        t2.setDelay(newDelay);

        if (ctrlTimer.isRunning() && t2.isRunning()) {
            ctrlTimer.stop();
            t2.stop();
        }

        ctrlTimer.start();
        t2.start();

        for (TrafficLightGroup g : lights)
            g.reset();
    }

    public void clearSim(){
        lights.clear();
        toUpdateCar.clear();
        caseUpdate.clear();
        needUpdate = false;
        stats.reset();
        iter.clear();
    }

    public void updateTimer(){
        ctrlTimer.stop();
        ctrlTimer = new Timer(50 * speed, createActionListener());
        t2 = new Timer(50 * speed, createGlobalListener());
        start();
        t2.start();
    }

    public ActionListener createActionListener() {
        return e -> {
            iter = new ArrayList<>(toUpdateCar);
            toUpdateCar.clear();

            for (Position p : iter) {
                if (p != null){
                    Vehicle v = CityMap.getCase(cityMap.getGrid(), p).getVehicle();
                    if (v != null)
                        needUpdate |= v.followPath(v.path, stats);
                }
            }

            if (needUpdate)
                gui.setUpdate(true);
        };
    }

    public ActionListener createGlobalListener(){
        return e -> {
            if (gui.isRunning() != ctrlTimer.isRunning()) {
                if (gui.isRunning()) {
                    nextChange = null;
                    caseUpdate.clear();
                    start();
                } else {
                    pause();
                }
            }
            if (!gui.isRunning() && !caseUpdate.isEmpty() && nextChange != null) {
                nextChange.update(cityMap, caseUpdate, stats);
                caseUpdate.clear();
                nextChange = null;
            }
        };
    }

    // --- Gestion des modifications de case ---
    public void setChange(String s) {
    if (s.startsWith("route:")) {
        String[] parts = s.split(":");
        String directionsStr = parts[1];
        boolean isIntersection = parts.length > 2 && parts[2].equals("intersection");

        String[] directionNames = directionsStr.split(",");
        Direction[] directions = new Direction[directionNames.length];
        for (int i = 0; i < directionNames.length; i++) {
            directions[i] = Direction.valueOf(directionNames[i]);
        }

        SetRoute.setSelectedDirections(directions);
        SetRoute.setIntersectionFlag(isIntersection);

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

    public void Spawn15Car(){
        Simulation.spawnRandomCars(15, stats, this);
    }

    public void removeUpdateCar(Position p) {
        toUpdateCar.removeIf(p::equals);
    }

    public void removeLight(TrafficLight light) {
        for (TrafficLightGroup g : lights) {
            if (g.getId() == light.getIDGroupe()) {
                g.remove(light);
                break;
            }
        }
    }

    public void addCar(Position p){
        addUpdateCar(p);
        addUpdate();
        gui.setUpdate(true);
    }

    // --- Getters / Setters ---
    public boolean getUpdate() { return needUpdate; }

    public void addUpdate() { needUpdate = true; }

    public void addUpdateCar(Position pos) {
        toUpdateCar.add(pos);
        addUpdate();
    }

    public Stack2 getCaseUpdate() { return caseUpdate; }

    public ArrayList<TrafficLightGroup> getLights() { return lights; }

    public int getSpeed() { return speed; }

    public void setSpeed(int n) { speed = n; }
    public boolean getTimerIsRunning(){return timerIsRunning;}
    public void setMap(CityMap map){
        this.cityMap = map;
        Simulation.setCityMap(map);
    }

    // --- Point d'entrée ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Controle::new);
    }
}