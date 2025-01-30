package Modele;



public class Simulation {
    private CityMap cityMap;

    public Simulation() {
        // Créer une carte de 10x10
        this.cityMap = new CityMap(10, 10);
        initializeMap();
    }

    private void initializeMap() {
        // Initialiser les routes
        for (int i = 0; i < 10; i++) {
            // Exemple de route horizontale
            cityMap.setRoad(new Position(i, 4), Direction.RIGHT, "full");
        }

        
        addBuilding(2, 2);
        addBuilding(4, 5);
        addBuilding(7, 8);
    }

    private void addBuilding(int x, int y) {

    }

    public static void main(String [] args){
        Simulation s = new Simulation();

        s.cityMap.displayMap();


    }

}
