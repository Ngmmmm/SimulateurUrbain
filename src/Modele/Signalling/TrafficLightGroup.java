package Modele.Signalling;

import java.util.*;
import java.util.concurrent.*;

public class TrafficLightGroup {
    private List<TrafficLight> lights;
    private int cycleTimeGreen = 10;  // Durée du feu vert
    private int cycleTimeOrange = 3;  // Durée du feu orange
    private int cycleTimeRed = 2;     // Durée du feu rouge (ajouté)
    private boolean isNSGreen; // Indique si les feux Nord-Sud sont verts
    private ScheduledExecutorService scheduler;
    public static int nbGroupe = 0;
    public int id;

    public TrafficLightGroup(List<TrafficLight> lights) {
        this.lights = lights;
        this.isNSGreen = true;
        this.scheduler = Executors.newScheduledThreadPool(1);
        startCycle();
        id = ++nbGroupe;
        setIdLights(lights);
    }

    private void startCycle() {
        scheduler.scheduleAtFixedRate(() -> {
            switchLights(LightColor.GREEN);
            sleep(cycleTimeGreen);
            switchLights(LightColor.ORANGE);
            sleep(cycleTimeOrange);
            isNSGreen = !isNSGreen; // Inverser le cycle après le rouge
        }, 0, cycleTimeGreen + cycleTimeOrange + cycleTimeRed, TimeUnit.SECONDS);
    }

    private void switchLights(LightColor newState) {
        for (TrafficLight light : lights) {
            if (light.isNorthSouth()) {
                light.setColor(isNSGreen ? newState : getOppositeState(newState));
            } else {
                light.setColor(isNSGreen ? getOppositeState(newState) : newState);
            }
        }
        System.out.println("Switched lights to " + newState + " (NSGreen: " + isNSGreen + ")");
    }

    private LightColor getOppositeState(LightColor state) {
        switch (state) {
            case GREEN: return LightColor.RED;
            case RED: return LightColor.GREEN;
            case ORANGE: return LightColor.ORANGE; // L'orange reste orange
            default: return LightColor.RED;
        }
    }

    private void sleep(int seconds) {
        try { Thread.sleep(seconds * 1000); } 
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void setIdLights(List<TrafficLight> lights) {
        for (TrafficLight light : lights) {
            light.setIDGroupe(this.id);
        }

    }
}