package Controle;

import Controle.SetObj.*;
import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.*;
import Vue.*;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

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

    // --- Constructeur ---
    public Controle() {
        initSim();
        ctrlTimer = new Timer(50 * speed, createActionListener());
        t2 = new Timer(50 * speed, e -> {
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
        });
        gui = new TrafficSimulatorGUI(cityMap, stats, this);
        timerIsRunning = true;
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

    public ActionListener createActionListener() {
        return e -> {
            ArrayList<Position> iter = new ArrayList<>(toUpdateCar);
            toUpdateCar.clear();

            for (Position p : iter) {
                Vehicle v = CityMap.getCase(cityMap.getGrid(), p).getVehicle();
                if (v != null)
                    needUpdate |= v.followPath(v.path, stats);
            }

            if (needUpdate)
                gui.setUpdate(true);
        };
    }

    // --- Gestion des modifications de case ---
    public void setChange(String s) {
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

    // --- Point d'entrée ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Controle::new);
    }
}