package Modele.Signalling;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

import Controle.Controle;

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
        setIdLights(lights);
        startCycle();
        id = ++nbGroupe;
        Controle.lights.add(this);
    }

    public void startCycle() {
        scheduler = Executors.newScheduledThreadPool(1);
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
        scheduler.shutdownNow();
    }

    public boolean isDown(){
        return scheduler.isShutdown();
    }

    private void setIdLights(List<TrafficLight> lights) {
        for (TrafficLight light : lights) {
            light.setIDGroupe(this.id);
        }

    }

    public void merge(TrafficLightGroup g){
        for(TrafficLight light : g.lights){
            add(light);
        }
    }

    public void add(TrafficLight t){
        boolean flag = true;
        for (TrafficLight f : lights){
            flag &= (f != t);
        }
        if (flag){
            lights.add(t);
            t.setIDGroupe(this.id);
        }

    }
    public void remove(TrafficLight t){
        lights.removeIf(new Predicate<TrafficLight>(){
            public boolean test(TrafficLight tList){
                return t == tList;
            }
        });
    }

    public int getId(){
        return id;
    }
}