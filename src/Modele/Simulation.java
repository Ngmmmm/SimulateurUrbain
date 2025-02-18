package Modele;

import Modele.Signalling.TrafficLight;
import Modele.Signalling.TrafficLightGroup;
import Modele.Vehicle.Car;
import Modele.Vehicle.Vehicle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {
    private CityMap cityMap;

    public Simulation() {
        this.cityMap = new CityMap(50, 50);
        initializeMap();
        spawnRandomCars();
    }

    private void initializeMap() {
        cityMap.setBuilding();

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
        
        List<TrafficLight> allLights = new ArrayList<>();
        allLights.add(new TrafficLight(new Position(23, 0), Direction.DOWN, cityMap));
        allLights.add(new TrafficLight(new Position(22, 2), Direction.RIGHT, cityMap));
        allLights.add(new TrafficLight(new Position(27, 1), Direction.LEFT, cityMap));
        allLights.add(new TrafficLight(new Position(26, 3), Direction.UP, cityMap));
        TrafficLightGroup groupe1 = new TrafficLightGroup(allLights);
    }

    public void spawnRandomCars() {
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {  // Ajouter 10 voitures
            Position randomPos;
            do {
                int x = rand.nextInt(50);
                int y = rand.nextInt(50);
                randomPos = new Position(y, x);
            } while (!cityMap.isValidPosition(randomPos.getY(), randomPos.getX()) || cityMap.getGrid()[randomPos.getY()][randomPos.getX()].getRoad() == null);

            Road road = cityMap.getRoad(randomPos);
            Direction direction = Direction.RIGHT; // Valeur par défaut

            if (road != null && road.getDirection().length > 0) {
                direction = road.getDirection()[0]; // Prendre la première direction disponible
            }
            ArrayList<Position> accessiblePosition = Pathfinding.findAllAccessiblePositions(cityMap, randomPos);
            Position destination = accessiblePosition.get(rand.nextInt(accessiblePosition.size()));

            Car newCar = new Car("Car" + Vehicle.nbCar, 2.0, randomPos, direction, cityMap, destination);
            cityMap.addVehicle(newCar, randomPos);

            System.out.println("Nouvelle voiture ajoutée :");
            System.out.println("ID: " + newCar.getId());
            System.out.println("Vitesse: " + newCar.getSpeed());
            System.out.println("Position: " + newCar.getPosition());
            System.out.println("Direction: " + newCar.getDirection());
            System.out.println("-------------------------------");
        }
    }

    public CityMap getCityMap() {
        return this.cityMap;
    }

    public void afficherCarte() {
        for (int y = 0; y < 50; y++) {
            for (int x = 0; x < 50; x++) {
                Road road = cityMap.getRoad(new Position(x, y));
    
                if (road == null) {
                    System.out.print(" "); // Building
                } else if (road.getDirection().length > 1) {
                    System.out.print("O"); // Intersection
                } else {
                    System.out.print("."); // Route simple
                }
            }
            System.out.println();
        }
    }
}
