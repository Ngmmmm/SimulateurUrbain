package Modele;

public class Simulation {
    private CityMap cityMap;

    public Simulation() {
        this.cityMap = new CityMap(50, 50);
        initializeMap();
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
            cityMap.setRoad(new Position(23, i), "Pointillé", Direction.DOWN);
            cityMap.setRoad(new Position(24, i), "Pointillé", Direction.DOWN);

            cityMap.setRoad(new Position(25, i), "Pointillé", Direction.UP);
            cityMap.setRoad(new Position(26, i), "Pointillé", Direction.UP);
        }

        // Routes verticales double sens
        for (int i = 14; i < 31; i++) {
            cityMap.setRoad(new Position(34, i), "Continue", Direction.DOWN);
            cityMap.setRoad(new Position(35, i), "Continue", Direction.UP);
        }
        for (int i = 33; i < 40; i++) {
            cityMap.setRoad(new Position(5, i), "Continue", Direction.DOWN);
            cityMap.setRoad(new Position(6, i), "Continue", Direction.UP);
        }


        // Sens uniques
        for (int i = 33; i < 40; i++) {
            cityMap.setRoad(new Position(40, i), "Continue", Direction.DOWN);
        }
        for (int i = 14; i < 31; i++) {
            cityMap.setRoad(new Position(10, i), "Continue", Direction.UP);
        }
        for (int i = 3; i < 12; i++) {
            cityMap.setRoad(new Position(47, i), "Continue", Direction.DOWN);
        }

        for (int i = 3; i < 12; i++) {
            cityMap.setRoad(new Position(47, i), "Continue", Direction.DOWN);
        }
    }

    public CityMap getCityMap() {
        return this.cityMap;
    }

    public static void main(String[] args) {
        Simulation s = new Simulation();
        AffichageTemp.affiche(s.cityMap.getGrid());
    }
}