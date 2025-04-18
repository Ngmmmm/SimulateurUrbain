package Modele.Enregistrer;

import Modele.*;
import Modele.Signalling.*;
import Modele.Vehicle.*;
import java.io.*;
import java.util.*;

public class GameLoader {

    /**
     * Charge une CityMap à partir d'un fichier de sauvegarde.
     *
     * @param fileName Le nom du fichier de sauvegarde (sans extension).
     * @return La CityMap chargée, ou null en cas d'erreur.
     */
    public static CityMap loadGame(String fileName, Statistique stats) {
        String filePath = "Save/" + fileName + ".txt";
        CityMap cityMap = null;
        Map<Integer, List<TrafficLight>> trafficLightGroups = new HashMap<>();

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
                    String[] roadData = line.substring(7).split(" ", 2);
                    String lineType = roadData[0];
                    Direction[] directions = parseDirections(roadData[1]);
                    Road road = new Road(directions, lineType);
                    cityMap.getGrid()[currentRow][currentCol].setRoad(road);
                } else if (line.startsWith("Batiment")) {
                    // Lecture des données du bâtiment
                    Building building = new Building();
                    cityMap.getGrid()[currentRow][currentCol].setBuilding(building);
                } else if (line.startsWith("Vehicule: ")) {
                    // Lecture des données du véhicule
                    String[] vehicleData = line.substring(10).split(" ");
                    String type = vehicleData[0].substring(0, vehicleData[0].length() - 1);  // Ajoute une colonne dans le fichier de sauvegarde pour stocker le type
                    String id = vehicleData[0];
                    String[] positionData = vehicleData[1].split(",");
                    int x = Integer.parseInt(positionData[0]);
                    int y = Integer.parseInt(positionData[1]);
                    Direction direction = Direction.fromString(vehicleData[2]);
                    int speed = Integer.parseInt(vehicleData[3]);
                    String[] destinationData = vehicleData[4].split(",");
                    int destX = Integer.parseInt(destinationData[0]);
                    int destY = Integer.parseInt(destinationData[1]);
                
                    Vehicle vehicle = createVehicle(type, id,speed, new Position(x, y), direction, new Position(destX, destY), cityMap, stats);
                    cityMap.getGrid()[currentRow][currentCol].setVehicle(vehicle);
                } else if (line.startsWith("Signalisation: ")) {
                    // Lecture des données de la signalisation
                    String[] signallingData = line.substring(15).split(" ");  // Diviser à partir de l'espace
                    String type = signallingData[0];  // "TrafficLights"

                    
                    // Extraire les coordonnées
                    String[] positionData = signallingData[1].split(",");  // Séparer les coordonnées
                    int x = Integer.parseInt(positionData[0]);
                    int y = Integer.parseInt(positionData[1]);
                    
                    // La direction est toujours présente ici
                    Direction direction = Direction.fromString(signallingData[2]);
                
                    // Créer la signalisation
                    Signalling signalling = createSignalling(type, new Position(x, y), direction, cityMap);
                
                    // Si c'est un feu de signalisation, récupérer l'ID de groupe
                    if (signalling instanceof TrafficLight && signallingData.length > 3) {
                        int groupID = Integer.parseInt(signallingData[3]);  // L'ID de groupe est à l'indice 3
                        ((TrafficLight) signalling).setIDGroupe(groupID);
                
                        // Ajouter le feu dans la liste du groupe
                        trafficLightGroups.computeIfAbsent(groupID, k -> new ArrayList<>()).add((TrafficLight) signalling);
                    }
                
                    // Ajouter la signalisation à la carte
                    cityMap.getGrid()[currentRow][currentCol].setSignalling(signalling);
                }
                

            }

            // Recréer les groupes de feux après lecture complète
            rebuildTrafficLightGroups(trafficLightGroups);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return cityMap;
    }

    /**
     * Recrée les groupes de feux et assigne les feux aux bons groupes.
     *
     * @param cityMap La carte de la ville
     * @param trafficLightGroups Les groupes de feux stockés temporairement
     */
    private static void rebuildTrafficLightGroups(Map<Integer, List<TrafficLight>> trafficLightGroups) {
        for (Map.Entry<Integer, List<TrafficLight>> entry : trafficLightGroups.entrySet()) {

            List<TrafficLight> lights = entry.getValue();

            // Créer un groupe de feux
            @SuppressWarnings("unused")
            TrafficLightGroup group = new TrafficLightGroup(lights);

        }
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
            directions[i] = Direction.fromString(directionNames[i]);
        }
        return directions;
    }


    /**
     * Crée un objet de signalisation en fonction du type.
     *
     * @param type Le type de signalisation (ex: "TrafficLight", "Stop", "NoEntry").
     * @param position La position de la signalisation.
     * @param direction La direction de la signalisation.
     * @param map La carte de la ville (nécessaire pour les feux tricolores).
     * @return Un objet Signalling.
     */
    private static Signalling createSignalling(String type, Position position, Direction direction, CityMap map) {
        switch (type) {
            case "TrafficLights" -> {
                return new TrafficLight(position, direction, map);
            }
            case "Stop" -> {
                return new Stop(position, direction);
            }
            case "NoEntry" -> {
                return new NoEntry(position, direction);
            }
            default -> throw new IllegalArgumentException("Type de signalisation inconnu : " + type);
        }
    }

    private static Vehicle createVehicle(String type, String id, int speed, Position position, Direction direction, Position destination, CityMap map, Statistique stats) {
    switch (type) {
        case "Car" -> {
            return new Car(id,speed, position, direction, map, destination, stats);
            }
        case "Truck" -> {
            return new Truck(id, speed, position, direction, map, destination, stats);
            }
        case "Bus" -> {
            return new Bus(id, speed, position, direction, map, destination, stats);
            }
        default -> throw new IllegalArgumentException("Type de véhicule inconnu : " + type);
    }
}

}
