package Modele.Signalling;

import java.util.*;

public class TrafficLightManager {
    private Map<Integer, TrafficLightGroup> groups = new HashMap<>();

    public TrafficLightManager(List<TrafficLight> allLights) {
        Map<String, List<TrafficLight>> groupedLights = new HashMap<>();

        for (TrafficLight light : allLights) {
            String key = getIntersectionKey(light);
            groupedLights.putIfAbsent(key, new ArrayList<>());
            groupedLights.get(key).add(light);
        }

        int groupId = 1;
        for (List<TrafficLight> lights : groupedLights.values()) {
            groups.put(groupId, new TrafficLightGroup(lights));
            groupId++;
        }
    }

    private String getIntersectionKey(TrafficLight light) {
        return light.getPosition().getX() + "-" +  light.getPosition().getY();
    }

    public void stopAllGroups() {
        for (TrafficLightGroup group : groups.values()) {
            group.stop();
        }
    }
}
