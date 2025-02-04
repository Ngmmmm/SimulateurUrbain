package Modele;

public class Simulation {
    private CityMap cityMap;

    public Simulation() {
        this.cityMap = new CityMap(50, 50);
        initializeMap();
    }

    private void initializeMap() {
        cityMap.setBuilding();

        // Routes principales horizontales (double file)
        for (int i = 0; i < 50; i++) {
            cityMap.setRoad(new Position(i, 8), Direction.RIGHT, "Avenue Est-Ouest 1");
            cityMap.setRoad(new Position(i, 9), Direction.RIGHT, "Avenue Est-Ouest 1");

            cityMap.setRoad(new Position(i, 25), Direction.RIGHT, "Avenue Est-Ouest 2");
            cityMap.setRoad(new Position(i, 26), Direction.RIGHT, "Avenue Est-Ouest 2");

            cityMap.setRoad(new Position(i, 42), Direction.RIGHT, "Avenue Est-Ouest 3");
            cityMap.setRoad(new Position(i, 43), Direction.RIGHT, "Avenue Est-Ouest 3");
        }

        // Routes principales verticales (double file)
        for (int i = 0; i < 50; i++) {
            cityMap.setRoad(new Position(5, i), Direction.DOWN, "Boulevard Nord-Sud 1");
            cityMap.setRoad(new Position(6, i), Direction.DOWN, "Boulevard Nord-Sud 1");

            cityMap.setRoad(new Position(20, i), Direction.DOWN, "Boulevard Nord-Sud 2");
            cityMap.setRoad(new Position(21, i), Direction.DOWN, "Boulevard Nord-Sud 2");

            cityMap.setRoad(new Position(35, i), Direction.DOWN, "Boulevard Nord-Sud 3");
            cityMap.setRoad(new Position(36, i), Direction.DOWN, "Boulevard Nord-Sud 3");
        }

        // Sens uniques
        for (int i = 10; i < 15; i++) {
            cityMap.setRoad(new Position(i, 15), Direction.RIGHT, "Sens Unique Est");
        }
        for (int i = 30; i < 35; i++) {
            cityMap.setRoad(new Position(i, 32), Direction.LEFT, "Sens Unique Ouest");
        }

        // Ronds-points carrés
        addRoundabout(15, 15);
        addRoundabout(15, 35);
        addRoundabout(35, 15);
        addRoundabout(35, 35);

        // Connexions aux ronds-points
        connectRoundabout(15, 15);
        connectRoundabout(15, 35);
        connectRoundabout(35, 15);
        connectRoundabout(35, 35);
    }

    private void addRoundabout(int x, int y) {
        // Rond-point carré 3x3
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                cityMap.setRoad(new Position(x + i, y + j), Direction.RIGHT, "Rond-Point");
            }
        }
    }

    private void connectRoundabout(int x, int y) {
        // Routes arrivant sur les ronds-points
        for (int i = 1; i <= 3; i++) {
            cityMap.setRoad(new Position(x, y - i - 1), Direction.DOWN, "Accès Rond-Point Nord");
            cityMap.setRoad(new Position(x, y + i + 1), Direction.UP, "Accès Rond-Point Sud");
            cityMap.setRoad(new Position(x - i - 1, y), Direction.RIGHT, "Accès Rond-Point Ouest");
            cityMap.setRoad(new Position(x + i + 1, y), Direction.LEFT, "Accès Rond-Point Est");
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
