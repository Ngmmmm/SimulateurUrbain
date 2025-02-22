package Modele.Enregistrer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.Vehicle;

public class GameLoader {

    /**
     * Charge une CityMap à partir d'un fichier de sauvegarde.
     *
     * @param fileName Le nom du fichier de sauvegarde (sans extension).
     * @return La CityMap chargée, ou null en cas d'erreur.
     */
    public static CityMap loadGame(String fileName) {
        String filePath = "Save/" + fileName + ".txt";
        CityMap cityMap = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Lecture des dimensions de la carte
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Carte: ")) {
                    String[] dimensions = line.split(" ")[1].split("x");
                    int rows = Integer.parseInt(dimensions[0]);
                    int cols = Integer.parseInt(dimensions[1]);
                    cityMap = new CityMap(rows, cols);
                    break;
                }
            }

            if (cityMap == null) {
                throw new IOException("Impossible de lire les dimensions de la carte.");
            }

            // Lecture des cases de la carte
            int currentRow = -1;
            int currentCol = -1;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Case: ")) {
                    // Lecture des coordonnées de la case
                    String[] coords = line.split(" ")[1].split(",");
                    currentRow = Integer.parseInt(coords[0]);
                    currentCol = Integer.parseInt(coords[1]);
                } else if (line.startsWith("Route: ")) {
                    // Lecture des données de la route
                    String[] roadData = line.split(" ")[1].split(" ");
                    String lineType = roadData[0];
                    Direction[] directions = parseDirections(roadData[1]);
                    Road road = new Road(directions, lineType);
                    cityMap.getGrid()[currentRow][currentCol].setRoad(road);
                } else if (line.startsWith("Batiment")) {
                    // Lecture des données du bâtiment (à implémenter si nécessaire)
                    // Exemple : Building building = new Building();
                    // cityMap.getGrid()[currentRow][currentCol].setBuilding(building);
                } else if (line.startsWith("Vehicule: ")) {
                    // Lecture des données du véhicule
                    String[] vehicleData = line.split(" ")[1].split(",");
                    String id = vehicleData[0];
                    int x = Integer.parseInt(vehicleData[1]);
                    int y = Integer.parseInt(vehicleData[2]);
                    Direction direction = Direction.valueOf(vehicleData[3]);
                    float fuel = Float.parseFloat(vehicleData[4]);
                    Vehicle vehicle = new Vehicle(id, 1.0, new Position(x, y), direction, 0.1f, cityMap, null);
                    vehicle.setSpeed(Double.parseDouble(vehicleData[5])); // Vitesse supplémentaire si nécessaire
                    cityMap.getGrid()[currentRow][currentCol].setVehicle(vehicle);
                } else if (line.startsWith("Signalisation: ")) {
                    // Lecture des données de la signalisation
                    String[] signallingData = line.split(" ")[1].split(",");
                    String type = signallingData[0];
                    int x = Integer.parseInt(signallingData[1]);
                    int y = Integer.parseInt(signallingData[2]);
                    Direction direction = Direction.valueOf(signallingData[3]);
                    Signalling signalling = createSignalling(type, new Position(x, y), direction);
                    cityMap.getGrid()[currentRow][currentCol].setSignalling(signalling);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return cityMap;
    }

    /**
     * Convertit une chaîne de directions en tableau de Direction.
     *
     * @param directionsStr La chaîne de directions (ex: "NORTH SOUTH").
     * @return Un tableau de Direction.
     */
    private static Direction[] parseDirections(String directionsStr) {
        String[] directionNames = directionsStr.split(" ");
        Direction[] directions = new Direction[directionNames.length];
        for (int i = 0; i < directionNames.length; i++) {
            directions[i] = Direction.valueOf(directionNames[i]);
        }
        return directions;
    }

    /**
     * Crée un objet de signalisation en fonction du type.
     *
     * @param type Le type de signalisation (ex: "TrafficLight", "Stop", "NoEntry").
     * @param position La position de la signalisation.
     * @param direction La direction de la signalisation.
     * @return Un objet Signalling.
     */
    private static Signalling createSignalling(String type, Position position, Direction direction) {
        switch (type) {
            case "TrafficLight":
                return new TrafficLight(position, direction);
            case "Stop":
                return new Stop(position, direction);
            case "NoEntry":
                return new NoEntry(position, direction);
            default:
                throw new IllegalArgumentException("Type de signalisation inconnu : " + type);
        }
    }
}