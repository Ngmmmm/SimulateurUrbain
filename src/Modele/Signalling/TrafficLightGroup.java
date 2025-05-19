package Modele.Signalling;

import Controle.Controle;
import java.util.*;
import java.util.concurrent.*;

/**
 * Gère un groupe de feux tricolores synchronisés selon une logique Nord-Sud / Est-Ouest.
 */
public class TrafficLightGroup {

    private List<TrafficLight> lights;
    private int cycleTimeGreen = 1000;   // Durée du vert en ms
    private int cycleTimeOrange = 300;   // Durée de l'orange en ms
    private int cycleTimeRed = 200;      // Durée du rouge en ms (utilisé pour décaler les cycles)
    private boolean isNSGreen;           // Vrai si Nord-Sud est vert
    private ScheduledExecutorService scheduler;
    public static int nbGroupe = 0;
    private int id;
    private Controle ctrl;

    /**
     * Initialise un groupe de feux avec un cycle automatique.
     */
    public TrafficLightGroup(List<TrafficLight> lights, Controle ctrl) {
        this.ctrl = ctrl;
        this.lights = lights;
        this.isNSGreen = true;
        id = ++nbGroupe;
        setIdLights(lights);
        ctrl.getLights().add(this);
        startCycle();
    }

    /**
     * Lance le cycle des feux (vert → orange → inverse).
     */
    public void startCycle() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            switchLights(LightColor.GREEN);
            sleep(cycleTimeGreen * ctrl.getSpeed());
            switchLights(LightColor.ORANGE);
            sleep(cycleTimeOrange * ctrl.getSpeed());
            isNSGreen = !isNSGreen;
        }, 0,
           (cycleTimeGreen + cycleTimeOrange + cycleTimeRed) * ctrl.getSpeed(),
           TimeUnit.MILLISECONDS);
    }

    /**
     * Change la couleur des feux selon leur direction et l'état du groupe.
     */
    public void switchLights(LightColor newState) {
        if (ctrl.getTimerIsRunning()){
            for (TrafficLight light : lights) {
                boolean isNS = light.isNorthSouth();
                LightColor color = (isNS == isNSGreen)? newState : getOppositeState(newState);
                light.setColor(color, ctrl);
            }
        }
    }

    /**
     * Renvoie la couleur opposée (rouge ↔ vert, orange = orange).
     */
    private LightColor getOppositeState(LightColor state) {
        return switch (state) {
            case GREEN -> LightColor.RED;
            case RED -> LightColor.GREEN;
            case ORANGE -> LightColor.ORANGE;
        };
    }

    /**
     * Pause d'attente dans le cycle (utilisée pour simuler le temps).
     */
    private void sleep(int milliseconds) {
        try { Thread.sleep(milliseconds); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Arrête le cycle des feux.
     */
    public void stop() {
        scheduler.shutdownNow();
    }

    /**
     * Redémarre le cycle des feux depuis le début.
     */
    public void reset() {
        stop();
        startCycle();
    }

    /**
     * Vérifie si le cycle est arrêté.
     */
    public boolean isDown() {
        return scheduler.isShutdown();
    }

    /**
     * Fusionne un autre groupe de feux dans ce groupe.
     */
    public void merge(TrafficLightGroup g) {
        for (TrafficLight light : g.lights){
            add(light);

        }
    }

    /**
     * Ajoute un feu au groupe s'il n'y est pas déjà.
     */
    public void add(TrafficLight t) {
        if (!lights.contains(t)) {
            lights.add(t);
            t.setIDGroupe(this.id);
        }
    }

    /**
     * Supprime un feu du groupe.
     */
    public void remove(TrafficLight t) {
        lights.remove(t);
    }

    /**
     * Attribue l'identifiant du groupe à tous les feux.
     */
    private void setIdLights(List<TrafficLight> lights) {
        for (TrafficLight light : lights) {
            light.setIDGroupe(this.id);
        }
    }

    /**
     * Retourne l'identifiant du groupe.
     */
    public int getId() {
        return id;
    }

    public List<TrafficLight> getLights(){
        return lights;
    }
}
