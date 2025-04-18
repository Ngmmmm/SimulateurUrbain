package Modele;

import Modele.Signalling.TrafficLight;
import Modele.Signalling.TrafficLightGroup;
import Modele.Vehicle.Bus;
import Modele.Vehicle.Car;
import Modele.Vehicle.Truck;
import Modele.Vehicle.Vehicle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Simulation {
    private static CityMap cityMap;

    public Simulation(Statistique stats) {
        cityMap = new CityMap(50, 50);
        initializeMap();
        cityMap.updateAllEdges();
        spawnRandomCars(10, stats);
    }

    private void initializeMap() {

        // Routes principales horizontales (Avenues)
        for (int i = 0; i < 50; i++) {
            cityMap.setRoad(new Position(i, 1), "Continue", Direction.LEFT);
            cityMap.setRoad(new Position(i, 2), "Continue", Direction.RIGHT);

            cityMap.setRoad(new Position(i, 12), "Continue", Direction.LEFT);
            cityMap.setRoad(new Position(i, 13), "Continue", Direction.RIGHT);

            cityMap.setRoad(new Position(i, 31), "Continue", Direction.LEFT);
            cityMap.setRoad(new Position(i, 32), "Continue", Direction.RIGHT);

            cityMap.setRoad(new Position(i, 40), "Continue", Direction.LEFT);
            cityMap.setRoad(new Position(i, 41), "Continue", Direction.RIGHT);
        }

        // Routes principales verticales (Boulevards)
        for (int i = 0; i < 50; i++) {
            cityMap.setRoad(new Position(23, i), "Pointillé", Direction.DOWN, Direction.RIGHT);
            cityMap.setRoad(new Position(24, i), "Pointillé", Direction.DOWN, Direction.LEFT);

            cityMap.setRoad(new Position(25, i), "Pointillé", Direction.UP, Direction.RIGHT);
            cityMap.setRoad(new Position(26, i), "Pointillé", Direction.UP, Direction.LEFT);
        }


        // Routes verticales double sens
        for (int i = 12; i < 31; i++) {cityMap.setRoad(new Position(34, i), "Continue", Direction.DOWN);}
        for (int i = 14; i < 33; i++) {cityMap.setRoad(new Position(35, i), "Continue", Direction.UP);}

        for (int i = 31; i < 40; i++) {cityMap.setRoad(new Position(5, i), "Continue", Direction.DOWN);}
        for (int i = 33; i < 42; i++) {cityMap.setRoad(new Position(6, i), "Continue", Direction.UP);}

        // Sens uniques
        for (int i = 1; i < 6; i++) {cityMap.setRoad(new Position(2, i), "Continue", Direction.DOWN);}
        for (int i = 2; i < 19; i++) {cityMap.setRoad(new Position(i, 6), "Continue", Direction.RIGHT);}
        for (int i = 6; i < 12; i++) {cityMap.setRoad(new Position(19, i), "Continue", Direction.DOWN);}
        
        for (int i = 3; i < 14; i++) {cityMap.setRoad(new Position(47, i), "Continue", Direction.UP);}

        for (int i = 14; i < 33; i++) {cityMap.setRoad(new Position(10, i), "Continue", Direction.UP);}
        
        for (int i = 17; i < 27; i++) {cityMap.setRoad(new Position(i, 26), "Continue", Direction.LEFT);}
        for (int i = 18; i < 27; i++) {cityMap.setRoad(new Position(16, i), "Continue", Direction.UP);}
        for (int i = 26; i < 31; i++) {cityMap.setRoad(new Position(16, i), "Continue", Direction.DOWN);}

        for (int i = 34; i < 45; i++) {cityMap.setRoad(new Position(i, 20), "Continue", Direction.RIGHT);}
        for (int i = 20; i < 31; i++) {cityMap.setRoad(new Position(45, i), "Continue", Direction.DOWN);}
        
        for (int i = 31; i < 40; i++) {cityMap.setRoad(new Position(40, i), "Continue", Direction.DOWN);}
        cityMap.setBuilding();

        cityMap.setNoEntry(new Position(19, 11));
        cityMap.setNoEntry(new Position(19, 11));
        cityMap.setNoEntry(new Position(47, 3));
        cityMap.setNoEntry(new Position(10, 14));
        cityMap.setNoEntry(new Position(16, 30));
        cityMap.setNoEntry(new Position(45, 30));
        cityMap.setNoEntry(new Position(40, 39));

        cityMap.setStop(new Position(19, 11));
        cityMap.setStop(new Position(19, 11));
        cityMap.setStop(new Position(47, 3));
        cityMap.setStop(new Position(10, 14));
        cityMap.setStop(new Position(16, 30));
        cityMap.setStop(new Position(45, 30));
        cityMap.setStop(new Position(40, 39));
        
        List<TrafficLight> allLights1 = new ArrayList<>();
        allLights1.add(new TrafficLight(new Position(23, 0), Direction.DOWN, cityMap));
        allLights1.add(new TrafficLight(new Position(22, 2), Direction.RIGHT, cityMap));
        allLights1.add(new TrafficLight(new Position(27, 1), Direction.LEFT, cityMap));
        allLights1.add(new TrafficLight(new Position(26, 3), Direction.UP, cityMap));
        TrafficLightGroup groupe1 = new TrafficLightGroup(allLights1);

        List<TrafficLight> allLights2 = new ArrayList<>();
        allLights2.add(new TrafficLight(new Position(23, 11), Direction.DOWN, cityMap));
        allLights2.add(new TrafficLight(new Position(22, 13), Direction.RIGHT, cityMap));
        allLights2.add(new TrafficLight(new Position(27, 12), Direction.LEFT, cityMap));
        allLights2.add(new TrafficLight(new Position(26, 14), Direction.UP, cityMap));
        TrafficLightGroup groupe2 = new TrafficLightGroup(allLights2);

        List<TrafficLight> allLights3 = new ArrayList<>();
        allLights3.add(new TrafficLight(new Position(23, 30), Direction.DOWN, cityMap));
        allLights3.add(new TrafficLight(new Position(22, 32), Direction.RIGHT, cityMap));
        allLights3.add(new TrafficLight(new Position(27, 31), Direction.LEFT, cityMap));
        allLights3.add(new TrafficLight(new Position(26, 33), Direction.UP, cityMap));
        TrafficLightGroup groupe3 = new TrafficLightGroup(allLights3);

        List<TrafficLight> allLights4 = new ArrayList<>();
        allLights4.add(new TrafficLight(new Position(23, 39), Direction.DOWN, cityMap));
        allLights4.add(new TrafficLight(new Position(22, 41), Direction.RIGHT, cityMap));
        allLights4.add(new TrafficLight(new Position(27, 40), Direction.LEFT, cityMap));
        allLights4.add(new TrafficLight(new Position(26, 42), Direction.UP, cityMap));
        TrafficLightGroup groupe4 = new TrafficLightGroup(allLights4);

        List<TrafficLight> allLights5 = new ArrayList<>();
        allLights5.add(new TrafficLight(new Position(36, 12), Direction.LEFT, cityMap));
        allLights5.add(new TrafficLight(new Position(33, 13), Direction.RIGHT, cityMap));
        allLights5.add(new TrafficLight(new Position(35, 14), Direction.UP, cityMap));
        TrafficLightGroup groupe5 = new TrafficLightGroup(allLights5);

        List<TrafficLight> allLights6 = new ArrayList<>();
        allLights6.add(new TrafficLight(new Position(7, 31), Direction.LEFT, cityMap));
        allLights6.add(new TrafficLight(new Position(4, 32), Direction.RIGHT, cityMap));
        allLights6.add(new TrafficLight(new Position(6, 33), Direction.UP, cityMap));
        TrafficLightGroup groupe6 = new TrafficLightGroup(allLights6);

        List<TrafficLight> allLights7 = new ArrayList<>();
        allLights7.add(new TrafficLight(new Position(7, 40), Direction.LEFT, cityMap));
        allLights7.add(new TrafficLight(new Position(4, 41), Direction.RIGHT, cityMap));
        allLights7.add(new TrafficLight(new Position(5, 39), Direction.DOWN, cityMap));
        TrafficLightGroup groupe7 = new TrafficLightGroup(allLights7);

        List<TrafficLight> allLights8 = new ArrayList<>();
        allLights8.add(new TrafficLight(new Position(36, 31), Direction.LEFT, cityMap));
        allLights8.add(new TrafficLight(new Position(33, 32), Direction.RIGHT, cityMap));
        allLights8.add(new TrafficLight(new Position(34, 30), Direction.DOWN, cityMap));
        TrafficLightGroup groupe8 = new TrafficLightGroup(allLights8);

    }

    public static void spawnRandomCars(int nbCarSpawn, Statistique stats) {
        Random rand = new Random();
        LinkedList<Position> spawnPositions = Pathfinding.FindSpawnPosition(cityMap);
    
        if (spawnPositions.isEmpty()) {
            System.out.println("Pas de spawn disponible.");
            return;
        }
    
        int nbCar = nbCarSpawn;
        int essai = 0;
        while (nbCar > 0 && essai < 50) {
            Position randomPos = spawnPositions.get(rand.nextInt(spawnPositions.size()));
    
            if (cityMap.isPositionOccupied(randomPos)) {
                essai++;
                continue; // Essaye une autre position
            }
    
            Road road = cityMap.getRoad(randomPos);
            Direction direction = (road != null && road.getDirection().length > 0) ? road.getDirection()[0] : Direction.RIGHT; // Par défaut
    
            cityMap.findAccessiblePos(randomPos);
            LinkedList<Position> accessible = cityMap.getAccessiblePosition();
            if (accessible.isEmpty()) {
                essai++;
                continue; // Essaye une autre position
            }
    
            Position destination = accessible.get(rand.nextInt(accessible.size()));
            while(!cityMap.haveBuildingNext(cityMap.getGrid()[destination.getY()][destination.getX()])){
                destination = accessible.get(rand.nextInt(accessible.size()));
            }
            Random rand2 = new Random();
            int nb = rand2.nextInt(3);
            switch (nb) {
                case 0 -> {
                    Car newCar = new Car("Car" + Vehicle.nbCar, 0, randomPos, direction, cityMap, destination, stats);
                    cityMap.addVehicle(newCar);
                }
                case 1 -> {
                    Truck newTruck = new Truck("Truck" + Vehicle.nbCar, 2, randomPos, direction, cityMap, destination, stats);
                    cityMap.addVehicle(newTruck);
                }
                default -> {
                    Bus newBus = new Bus("Bus" + Vehicle.nbCar, 3, randomPos, direction, cityMap, destination, stats);
                    cityMap.addVehicle(newBus);
                }
            }
            nbCar--;
        }
    }
    
    public CityMap getCityMap() {
        return this.cityMap;
    }
}
