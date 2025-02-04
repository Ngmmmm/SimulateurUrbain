package Modele;
import Vue.UpdateInter;


public class Simulation {
    private CityMap cityMap;

    public Simulation(UpdateInter update) {
        // Créer une carte de 10x10
        this.cityMap = new CityMap(50, 50, update);
        initializeMap();
    }

    private void initializeMap() {
        // Initialiser les routes
        for (int i = 0; i < 10; i++) {
            // Exemple de route horizontale
            cityMap.setRoad(new Position(i, 4), Direction.RIGHT, "full");
        }
        cityMap.setBuilding();

    }


}
