package Modele;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Modele.Vehicle.Vehicle;

public class Pathfinding {

    
    private Pathfinding() {
        // Empêcher l'instanciation de la classe statique
    }

    public static LinkedList<Position> findShortestPath(CityMap cityMap, Position start, Position goal) {
        int rows = cityMap.getGrid().length;
        int cols = cityMap.getGrid()[0].length;

        boolean[][] visited = new boolean[rows][cols]; // Cases visitées
        double[][] distances = new double[rows][cols]; // Distances minimales
        Position[][] previous = new Position[rows][cols]; // Chemin pour reconstituer

        // Initialisation des distances
        for (int i = 0; i < rows; i++) {
            Arrays.fill(distances[i], Double.MAX_VALUE);
        }
        distances[start.getY()][start.getX()] = 0;

        // File de priorité (Dijkstra)
        PriorityQueue<Position> openSet = new PriorityQueue<>(Comparator.comparingDouble(p -> distances[p.getY()][p.getX()]));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Position current = openSet.poll(); // Extraction du min

            if (current.equals(goal)) {
                return reconstructPath(previous, goal);
            }

            int x = current.getX(), y = current.getY();
            if (visited[y][x]) continue; // Si déjà visité, on passe
            visited[y][x] = true;

            Road road = cityMap.getRoad(current);
            if (road != null) {
                for (Direction direction : road.getDirection()) {
                    Position neighbor = direction.move(current);

                    if (!cityMap.isValidPosition(neighbor.getY(), neighbor.getX()) ||
                        visited[neighbor.getY()][neighbor.getX()] ||
                        cityMap.getRoad(neighbor) == null) {
                        continue;
                    }

                    Road neighborRoad = cityMap.getRoad(neighbor);
                    double baseWeight = neighborRoad.getWeight(); // Poids de la route
                    double trafficFactor = neighborRoad.getTrafficFactor(); // Congestion
                    double delay = 0;

                    // Gestion des feux rouges
                    if (neighborRoad.hasTrafficLight()) {
                        delay += neighborRoad.getTrafficLightDelay();
                    }

                    // Gestion des stops
                    if (neighborRoad.hasStopSign()) {
                        delay += 1; // Supposons 1 unité de temps pour un stop
                    }

                    // Réduction de l’impact du trafic si route à double voie
                    if (neighborRoad.isDoubleLane()) {
                        trafficFactor *= 0.5; // Réduction de moitié
                    }

                    // --- Gestion des véhicules sur la route ---
                    double vehiclePenalty = 0;
                    Vehicle vehicle = cityMap.getVehicleAt(neighbor);
                    if (vehicle != null) {
                        double distanceToGoal = Math.sqrt(Math.pow(goal.getX() - neighbor.getX(), 2) + Math.pow(goal.getY() - neighbor.getY(), 2));
                        vehiclePenalty = 5.0 / (1 + distanceToGoal); // Plus la voiture est proche, plus la pénalité est forte
                    }

                    double newDist = distances[y][x] + (baseWeight * trafficFactor) + delay + vehiclePenalty;

                    if (newDist < distances[neighbor.getY()][neighbor.getX()]) {
                        distances[neighbor.getY()][neighbor.getX()] = newDist;
                        previous[neighbor.getY()][neighbor.getX()] = current;
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new LinkedList<>(); // Aucun chemin trouvé
    }
  
    private static LinkedList<Position> reconstructPath(Position[][] previous, Position goal) {
        LinkedList<Position> path = new LinkedList<>();
        Position current = goal;

        while (current != null) {
            path.addFirst(current);
            current = previous[current.getY()][current.getX()];
        }
        return path;
    }




    public static LinkedList<Position> findAllAccessiblePositions(CityMap cityMap, Position start) {
        int rows = cityMap.getGrid().length;
        int cols = cityMap.getGrid()[0].length;
    
        boolean[][] visited = new boolean[rows][cols]; // Marqueur des cases visitées
        double[][] distances = new double[rows][cols]; // Distance minimale trouvée
    
        // Initialisation des distances à l'infini
        for (int i = 0; i < rows; i++) {
            Arrays.fill(distances[i], Double.MAX_VALUE);
        }
        distances[start.getY()][start.getX()] = 0;
    
        // PriorityQueue pour explorer les positions avec la plus petite distance en premier
        PriorityQueue<Position> openSet = new PriorityQueue<>(Comparator.comparingDouble(p -> distances[p.getY()][p.getX()]));
        openSet.add(start);
    
        while (!openSet.isEmpty()) {
            // Extraire la position avec la plus petite distance
            Position current = openSet.poll();
    
            // Si déjà visité, on passe
            if (visited[current.getY()][current.getX()]) {
                continue;
            }
            visited[current.getY()][current.getX()] = true;
    
            // Explorer les voisins valides
            Road road = cityMap.getRoad(current);
            if (road != null) {
                for (Direction direction : road.getDirection()) {
                    Position neighbor = direction.move(current);
    
                    if (!cityMap.isValidPosition(neighbor.getY(), neighbor.getX()) ||
                        visited[neighbor.getY()][neighbor.getX()] ||
                        cityMap.getRoad(neighbor) == null) {
                        continue;
                    }
    
                    double newDist = distances[current.getY()][current.getX()] + 1;
                    if (newDist < distances[neighbor.getY()][neighbor.getX()]) {
                        distances[neighbor.getY()][neighbor.getX()] = newDist;
                        openSet.add(neighbor);
                    }
                }
            }
        }
    
        // Collecter toutes les positions accessibles
        LinkedList<Position> accessiblePositions = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (distances[i][j] != Double.MAX_VALUE) {
                    accessiblePositions.add(new Position(j, i)); // (x, y)
                }
            }
        }
    
        return accessiblePositions;
    }
    


    public static LinkedList<Position> FindSpawnPosition(CityMap map){
        LinkedList<Position> resultat = new LinkedList<>();
        int rows = map.getRows();
        int cols = map.getCols();

        Case[][]grid = map.getGrid();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 4; j++) {  // Boucle sur les 4 bords
                int x = (j == 0) ? 0 : (j == 1) ? i : (j == 2) ? rows - 1 : i;
                int y = (j == 0) ? i : (j == 1) ? 0 : (j == 2) ? i : cols - 1;
        
                if (grid[y][x].isRoad()) {
                    Direction[] directions = grid[y][x].getRoad().getDirection();
                    Position pos = new Position(x, y);
                    boolean test = false;
        
                    for (Direction dir : directions) {
                        if (dir.move(pos).posIsInMap(rows, cols)) {
                            test = true;
                            break;
                        }
                    }
        
                    if (test) {
                        resultat.add(pos);
                    }
                }
            }
        }
        
        return resultat;


    }

    
    
}
