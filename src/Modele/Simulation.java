package Modele;

import Modele.Signalling.*;
import Modele.Vehicle.*;
import java.util.*;

import Controle.Controle;

public class Simulation {
    private static CityMap cityMap;
    private final Controle ctrl;
    private static final Random RANDOM = new Random();

    public Simulation(Statistique stats, Controle ctrl) {
        this.ctrl = ctrl;
        cityMap = new CityMap(50, 50);
        initializeMap();
        cityMap.updateAllEdges();
        spawnRandomCars(10, stats, ctrl);
    }

    private void initializeMap() {
        setupRoads();
        cityMap.setBuilding();
        setupNoEntryAndStops();
        setupTrafficLightGroups();
    }

    // 1) Toutes les avenues horizontales et boulevards verticaux
    private void setupRoads() {
        // Avenues horizontales
        int[] ys = {1, 2, 12, 13, 31, 32, 40, 41};
        for (int x = 0; x < 50; x++) {
            for (int i = 0; i < ys.length; i++) {
                Direction dir = (i % 2 == 0) ? Direction.LEFT : Direction.RIGHT;
                cityMap.setRoad(new Position(x, ys[i]), "Continue", dir);
            }
        }

        // Boulevards verticaux pointillés
        int[] xs = {23, 24, 25, 26};
        Direction[][] patterns = {
            {Direction.DOWN, Direction.RIGHT},
            {Direction.DOWN, Direction.LEFT},
            {Direction.UP,   Direction.RIGHT},
            {Direction.UP,   Direction.LEFT}
        };
        for (int xIndex = 0; xIndex < xs.length; xIndex++) {
            int x = xs[xIndex];
            for (int y = 0; y < 50; y++) {
                cityMap.setRoad(new Position(x, y),
                                "Pointillé",
                                patterns[xIndex][0],
                                patterns[xIndex][1]);
            }
        }

        // Autres axes et sens uniques / bidirectionnels
        for (int i = 12; i < 31; i++) cityMap.setRoad(new Position(34, i), "Continue", Direction.DOWN);
        for (int i = 14; i < 33; i++) cityMap.setRoad(new Position(35, i), "Continue", Direction.UP);

        for (int i = 31; i < 40; i++) cityMap.setRoad(new Position(5, i), "Continue", Direction.DOWN);
        for (int i = 33; i < 42; i++) cityMap.setRoad(new Position(6, i), "Continue", Direction.UP);

        for (int i = 1;  i < 6;  i++) cityMap.setRoad(new Position(2, i),  "Continue", Direction.DOWN);
        for (int i = 2;  i < 19; i++) cityMap.setRoad(new Position(i, 6),  "Continue", Direction.RIGHT);
        for (int i = 6;  i < 12; i++) cityMap.setRoad(new Position(19, i), "Continue", Direction.DOWN);
        for (int i = 3;  i < 14; i++) cityMap.setRoad(new Position(47, i), "Continue", Direction.UP);
        for (int i = 14; i < 33; i++) cityMap.setRoad(new Position(10, i), "Continue", Direction.UP);

        for (int i = 17; i < 27; i++) cityMap.setRoad(new Position(i, 26), "Continue", Direction.LEFT);
        for (int i = 18; i < 27; i++) cityMap.setRoad(new Position(16, i), "Continue", Direction.UP);
        for (int i = 26; i < 31; i++) cityMap.setRoad(new Position(16, i), "Continue", Direction.DOWN);

        for (int i = 34; i < 45; i++) cityMap.setRoad(new Position(i, 20), "Continue", Direction.RIGHT);
        for (int i = 20; i < 31; i++) cityMap.setRoad(new Position(45, i), "Continue", Direction.DOWN);
        for (int i = 31; i < 40; i++) cityMap.setRoad(new Position(40, i), "Continue", Direction.DOWN);
    }

    // 2) NoEntry et Stops en une seule passe
    private void setupNoEntryAndStops() {
        Position[] points = {
            new Position(19, 11),
            new Position(47, 3),
            new Position(10, 14),
            new Position(16, 30),
            new Position(45, 30),
            new Position(40, 39)
        };
        for (Position p : points) {
            cityMap.setNoEntry(p);
            cityMap.setStop(p);
        }
    }

    // 3) Facteur répétitif : création des TrafficLightGroup
    private void setupTrafficLightGroups() {
        initTrafficLightGroup(
            new int[][] { {23,0}, {22,2}, {27,1}, {26,3} },
            new Direction[] { Direction.DOWN, Direction.RIGHT, Direction.LEFT, Direction.UP }
        );
        initTrafficLightGroup(
            new int[][] { {23,11}, {22,13}, {27,12}, {26,14} },
            new Direction[] { Direction.DOWN, Direction.RIGHT, Direction.LEFT, Direction.UP }
        );
        initTrafficLightGroup(
            new int[][] { {23,30}, {22,32}, {27,31}, {26,33} },
            new Direction[] { Direction.DOWN, Direction.RIGHT, Direction.LEFT, Direction.UP }
        );
        initTrafficLightGroup(
            new int[][] { {23,39}, {22,41}, {27,40}, {26,42} },
            new Direction[] { Direction.DOWN, Direction.RIGHT, Direction.LEFT, Direction.UP }
        );
        initTrafficLightGroup(
            new int[][] { {36,12}, {33,13}, {35,14} },
            new Direction[] { Direction.LEFT, Direction.RIGHT, Direction.UP }
        );
        initTrafficLightGroup(
            new int[][] { {7,31}, {4,32}, {6,33} },
            new Direction[] { Direction.LEFT, Direction.RIGHT, Direction.UP }
        );
        initTrafficLightGroup(
            new int[][] { {7,40}, {4,41}, {5,39} },
            new Direction[] { Direction.LEFT, Direction.RIGHT, Direction.DOWN }
        );
        initTrafficLightGroup(
            new int[][] { {36,31}, {33,32}, {34,30} },
            new Direction[] { Direction.LEFT, Direction.RIGHT, Direction.DOWN }
        );
    }

    private void initTrafficLightGroup(int[][] coords, Direction[] dirs) {
        List<TrafficLight> lights = new ArrayList<>(coords.length);
        for (int i = 0; i < coords.length; i++) {
            Position pos = new Position(coords[i][0], coords[i][1]);
            lights.add(new TrafficLight(pos, dirs[i], cityMap));
        }
        new TrafficLightGroup(lights, ctrl);
    }

    // 4) Spawn optimisé : un seul Random et boucle claire
    public static void spawnRandomCars(int nbCars, Statistique stats, Controle ctrl) {
        LinkedList<Position> spawnPositions = Pathfinding.FindSpawnPosition(cityMap); 
        if (spawnPositions.isEmpty()) {
            System.out.println("Pas de spawn disponible.");
            return;
        }

        int attempts = 0;
        while (nbCars > 0 && attempts < 50) {
            Position spawn = spawnPositions.get(RANDOM.nextInt(spawnPositions.size()));
            if (cityMap.isPositionOccupied(spawn)) {
                attempts++;
                continue;
            }

            cityMap.findAccessiblePos(spawn);
            LinkedList<Position> accessible = cityMap.getAccessiblePosition();
            if (accessible.isEmpty()) {
                attempts++;
                continue;
            }

            Position dest;
            do {
                dest = accessible.get(RANDOM.nextInt(accessible.size()));
            } while (!cityMap.haveBuildingNext(cityMap.getGrid()[dest.getY()][dest.getX()]));

            Vehicle v;
            switch (RANDOM.nextInt(3)) {
                case 0: v = new Car("Car" + Vehicle.nbCar, 0, spawn, cityMap, dest, stats, ctrl); break;
                case 1: v = new Truck("Truck" + Vehicle.nbCar, 2, spawn, cityMap, dest, stats, ctrl); break;
                default: v = new Bus("Bus" + Vehicle.nbCar, 3, spawn, cityMap, dest, stats, ctrl);
            }
            v.followAllPath(stats);
            nbCars--;
        }
    }

    public CityMap getCityMap() {
        return cityMap;
    }

    public static void setCityMap(CityMap cityMap){
        Simulation.cityMap = cityMap;
    }
}
