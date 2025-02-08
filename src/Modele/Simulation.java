package Modele;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Simulation {
    private CityMap cityMap;

    public Simulation() {
        this.cityMap = new CityMap(50, 50);
        initializeMap();
    }
    //54 = 32 + 8 + 14
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
            cityMap.setRoad(new Position(23, i), "Pointillé", Direction.DOWN);
            //cityMap.setRoad(new Position(23, i), "Pointillé", Direction.DOWN, Direction.RIGHT);
            cityMap.setRoad(new Position(24, i), "Pointillé", Direction.DOWN);
            //cityMap.setRoad(new Position(24, i), "Pointillé", Direction.DOWN, Direction.LEFT);

            cityMap.setRoad(new Position(25, i), "Pointillé", Direction.UP);
            //cityMap.setRoad(new Position(25, i), "Pointillé", Direction.UP, Direction.RIGHT);
            cityMap.setRoad(new Position(26, i), "Pointillé", Direction.UP);
            //cityMap.setRoad(new Position(26, i), "Pointillé", Direction.UP, Direction.LEFT);
        }

        // Routes verticales double sens
        for (int i = 12; i < 31; i++) {cityMap.setRoad(new Position(34, i), "Continue", Direction.DOWN);}
        for (int i = 14; i < 33; i++) {cityMap.setRoad(new Position(35, i), "Continue", Direction.UP);}
        for (int i = 31; i < 40; i++) {cityMap.setRoad(new Position(5, i), "Continue", Direction.DOWN);}
        for (int i = 33; i < 41; i++) {cityMap.setRoad(new Position(6, i), "Continue", Direction.UP);}

        // Sens uniques
        for (int i = 1; i < 6; i++) {cityMap.setRoad(new Position(2, i), "Continue", Direction.DOWN);}
        for (int i = 2; i < 19; i++) {cityMap.setRoad(new Position(i, 6), "Continue", Direction.RIGHT);}
        for (int i = 6; i < 12; i++) {cityMap.setRoad(new Position(19, i), "Continue", Direction.DOWN);}
        
        for (int i = 3; i < 14; i++) {cityMap.setRoad(new Position(47, i), "Continue", Direction.UP);}

        for (int i = 31; i < 40; i++) {cityMap.setRoad(new Position(40, i), "Continue", Direction.DOWN);}
        
        for (int i = 16; i < 27; i++) {cityMap.setRoad(new Position(i, 26), "Continue", Direction.LEFT);}
        for (int i = 18; i < 27; i++) {cityMap.setRoad(new Position(16, i), "Continue", Direction.UP);}
        
        System.out.println(Arrays.toString(cityMap.getRoad(new Position(16,26)).getDirection()));

        /*
        for (int i = 34; i < 45; i++) {
            cityMap.setRoad(new Position(i, 20), "Continue", Direction.RIGHT);
        }
        for (int i = 20; i < 31; i++) {
            cityMap.setRoad(new Position(45, i), "Continue", Direction.DOWN);
        }
        
        
        for (int i = 14; i < 31; i++) {
            cityMap.setRoad(new Position(10, i), "Continue", Direction.UP);
        }
        
        
        
         */

        setIntersections();
    }

    private void setIntersections() {
        int i = 0;
        for (int x = 1; x < 49; x++) {
            for (int y = 1; y < 49; y++) {
                if (cityMap.isRoad(new Position(x, y))) {
                    List<Direction> directions = new ArrayList<>();

                    if (cityMap.isRoad(new Position(x - 1, y))) {
                        Road leftRoad = cityMap.getRoad(new Position(x - 1, y));
                        if (canConnect(leftRoad, Direction.LEFT)) {
                            directions.add(Direction.LEFT);
                        }
                    }
                    if (cityMap.isRoad(new Position(x + 1, y))) {
                        Road rightRoad = cityMap.getRoad(new Position(x + 1, y));
                        if (canConnect(rightRoad, Direction.RIGHT)) {
                            directions.add(Direction.RIGHT);
                        }
                    }
                    if (cityMap.isRoad(new Position(x, y - 1))) {
                        Road upRoad = cityMap.getRoad(new Position(x, y - 1));
                        if (canConnect(upRoad, Direction.UP)) {
                            directions.add(Direction.UP);
                        }
                    }
                    if (cityMap.isRoad(new Position(x, y + 1))) {
                        Road downRoad = cityMap.getRoad(new Position(x, y + 1));
                        if (canConnect(downRoad, Direction.DOWN)) {
                            directions.add(Direction.DOWN);
                        }
                    }

                    // Si au moins 2 directions sont valides, c'est une intersection
                    if (directions.size() > 1) {
                        System.out.print("Intersection à (" + x + ", " + y + ") avec directions : ");
                        for (Direction dir : directions) {
                            System.out.print(dir + " ");
                        }
                        System.out.println();
                        i++;

                        // Fusionner les directions des routes voisines
                        cityMap.setRoad(new Position(x, y), "Intersection", 
                            directions.toArray(new Direction[0]));
                    }
                }
            }
        }
        System.out.println(i);
    }


    
    // Vérifie si une route peut être connectée à une autre en fonction de ses directions valides.
    private boolean canConnect(Road adjacentRoad, Direction checkDirection) {
        if (adjacentRoad == null) return false;

        // Vérifie si la direction existe dans la liste des directions valides
        for (Direction dir : adjacentRoad.getDirection()) {
            if (dir == checkDirection) {
                return true;
            }
        }
        return false;
    }

    public CityMap getCityMap() {
        return this.cityMap;
    }

    public static void main(String[] args) {
        Simulation s = new Simulation();
        AffichageTemp.affiche(s.cityMap.getGrid());
    }
}