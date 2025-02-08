package Modele.Signalling;

import java.util.*;

import Modele.Position;

public class TrafficLightManager {
    private List<TrafficLightGroup> groups;

    public TrafficLightManager(List<TrafficLight> allLights) {
        this.groups = new ArrayList<>();
        createGroups(allLights);
    }

    private void createGroups(List<TrafficLight> allLights) {
        Map<String, Set<TrafficLight>> intersections = new HashMap<>();
        
        // 1️⃣ Regrouper les feux appartenant à la même intersection
        for (TrafficLight light : allLights) {
            String key = getIntersectionKey(light.getPosition());
            intersections.putIfAbsent(key, new HashSet<>());
            intersections.get(key).add(light);
        }

        // 2️⃣ Créer les groupes en respectant la règle des 4 feux max avec 2 directions opposées
        for (Set<TrafficLight> intersectionLights : intersections.values()) {
            List<TrafficLight> nsLights = new ArrayList<>();
            List<TrafficLight> ewLights = new ArrayList<>();

            for (TrafficLight light : intersectionLights) {
                if (light.isNorthSouth()) {
                    nsLights.add(light);
                } else {
                    ewLights.add(light);
                }
            }

            // Un groupe unique par intersection (max 4 feux)
            List<TrafficLight> group = new ArrayList<>();
            group.addAll(nsLights);
            group.addAll(ewLights);
            
            if (!group.isEmpty()) {
                groups.add(new TrafficLightGroup(group));
            }
        }
    }

    private String getIntersectionKey(Position pos) {
        // Clé basée sur la proximité (ex: X-Y regroupe les feux voisins)
        return pos.getX() / 2 + "-" + pos.getY() / 2;
    }

    public void stopAllGroups() {
        for (TrafficLightGroup group : groups) {
            group.stop();
        }
    }
}
