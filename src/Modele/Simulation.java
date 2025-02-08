package Modele;

import java.util.Arrays;

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
            //cityMap.setRoad(new Position(23, i), "Pointillé", Direction.DOWN);
            cityMap.setRoad(new Position(23, i), "Pointillé", Direction.DOWN, Direction.RIGHT);
            //cityMap.setRoad(new Position(24, i), "Pointillé", Direction.DOWN);
            cityMap.setRoad(new Position(24, i), "Pointillé", Direction.DOWN, Direction.LEFT);

            //cityMap.setRoad(new Position(25, i), "Pointillé", Direction.UP);
            cityMap.setRoad(new Position(25, i), "Pointillé", Direction.UP, Direction.RIGHT);
            //cityMap.setRoad(new Position(26, i), "Pointillé", Direction.UP);
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

        for (int i = 31; i < 40; i++) {cityMap.setRoad(new Position(40, i), "Continue", Direction.DOWN);}
        
        for (int i = 17; i < 27; i++) {cityMap.setRoad(new Position(i, 26), "Continue", Direction.LEFT);}
        for (int i = 18; i < 27; i++) {cityMap.setRoad(new Position(16, i), "Continue", Direction.UP);}
        for (int i = 26; i < 31; i++) {cityMap.setRoad(new Position(16, i), "Continue", Direction.DOWN);}
        System.out.println(Arrays.toString(cityMap.getRoad(new Position(26,26)).getDirection()));


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

        countIntersections();
    }

    public void countIntersections() {
        int intersections = 0;
        
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                Road road = cityMap.getRoad(new Position(x, y));
                if (road != null && road.getDirection().length > 1) {
                    intersections++;
                }
            }
        }
        
        System.out.println(intersections);
    }

    public CityMap getCityMap() {
        return this.cityMap;
    }

    public void afficherCarte() {
        for (int y = 0; y < 50; y++) {
            for (int x = 0; x < 50; x++) {
                Road road = cityMap.getRoad(new Position(x, y));
    
                if (road == null) {
                    System.out.print(" "); // Pas de route
                } else if (road.getDirection().length > 1) {
                    System.out.print("O"); // Intersection
                } else {
                    System.out.print("."); // Route simple
                }
            }
            System.out.println(); // Nouvelle ligne pour la ligne suivante
        }
    }
    

    public static void main(String[] args) {
        Simulation s = new Simulation();
        // AffichageTemp.affiche(s.cityMap.getGrid());
        s.afficherCarte();
        
    }
}