package Modele;

import Modele.Vehicle.Vehicle;
import java.util.*;

public class Pathfinding {

    private Pathfinding() {
        // Empêcher l'instanciation de la classe statique
    }

    /**
     * Applique l'algorithme de dijkstra pour trouver le chemin le plus court pour aller de start à goal
     * @param cityMap Carte sur laquel se déplacer
     * @param start Position de départ du trajet
     * @param goal Position d'arrivée du trajet
     * @return LinkedList<Position> contenant les positions successivent que la voiture doit prendre
     */
    public static LinkedList<Position> findShortestPath(CityMap cityMap, Position start, Position goal) {
        int rows = cityMap.getGrid().length;
        int cols = cityMap.getGrid()[0].length;

        boolean[][] visited = new boolean[rows][cols]; // Cases visitées
        double[][] distances = new double[rows][cols]; // Distances minimales
        Position[][] previous = new Position[rows][cols]; // Chemin pour reconstituer

        // Initialisation des distances
        for (double[] row : distances) {
            Arrays.fill(row, Double.MAX_VALUE);
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
                    double baseWeight = neighborRoad.getWeight();
                    double trafficFactor = neighborRoad.getTrafficFactor();
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
                    if (neighborRoad.isDoubleLane() && trafficFactor > 1.5) {
                        trafficFactor *= 0.9;  // aide un peu quand il y a beaucoup de trafic
                    }

                    // --- Gestion des véhicules sur la route ---
                    double vehiclePenalty = 0;
                    Vehicle vehicle = cityMap.getVehicleAt(neighbor);
                    if (vehicle != null) {
                        if (!vehicle.isMoving() && detectDeadlock(cityMap, vehicle)) {
                            // Deadlock détecté -> coût infini
                            vehiclePenalty = 1000.0;//Double.MAX_VALUE;
                            
                        } else {
                            double distanceToGoal = Math.sqrt(Math.pow(goal.getX() - neighbor.getX(), 2) + Math.pow(goal.getY() - neighbor.getY(), 2));
                            vehiclePenalty = 5.0 / (1 + distanceToGoal);
                        }
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

    /**
     * étape final de l'algorithme de Dijkstra pour reconstituer le chemin pris une fois que goal à été atteint
    */
    private static LinkedList<Position> reconstructPath(Position[][] previous, Position goal) {
        LinkedList<Position> path = new LinkedList<>();
        Position current = goal;

        while (current != null) {
            path.addFirst(current);
            current = previous[current.getY()][current.getX()];
        }
        return path;
    }

    /**
     * Trouve toutes les cases accessibles à partir d'une position initiale
     * @param cityMap Carte sur laquel se déplacer
     * @param start Position initiale
     * @return LinkedList<Position> des positions accessibles
     */
    public static LinkedList<Position> findAllAccessiblePositions(CityMap cityMap, Position start) {
        int rows = cityMap.getGrid().length;
        int cols = cityMap.getGrid()[0].length;
    
        boolean[][] visited = new boolean[rows][cols]; // Marqueur des cases visitées
        double[][] distances = new double[rows][cols]; // Distance minimale trouvée
    
        // Initialisation des distances à l'infini
        for (double[] row : distances) {
            Arrays.fill(row, Double.MAX_VALUE);
        }
        distances[start.getY()][start.getX()] = 0;
    
        // PriorityQueue pour explorer les positions avec la plus petite distance en premier
        PriorityQueue<Position> openSet = new PriorityQueue<>(Comparator.comparingDouble(p -> distances[p.getY()][p.getX()]));
        openSet.add(start);
    
        while (!openSet.isEmpty()) {
            Position current = openSet.poll();
    
            // Si déjà visité, on passe
            if (visited[current.getY()][current.getX()]) continue;
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
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!(x == start.getX() && y == start.getY()) && distances[y][x] != Double.MAX_VALUE) {
                    accessiblePositions.add(new Position(x, y));
                }
            }
        }
    
        return accessiblePositions;
    }

    /**
     * Créer la liste des Position de spawn valide (sur les bords de la CityMap)
     * @param map Carte sur laquel se déplacer
     * @return LinkedList<Position> liste des Positions valide
     */
    public static LinkedList<Position> FindSpawnPosition(CityMap map) {
        LinkedList<Position> result = new LinkedList<>();
        int rows = map.getRows();
        int cols = map.getCols();
        Case[][] grid = map.getGrid();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 4; j++) {  // Boucle sur les 4 bords
                int x = (j == 0) ? 0 : (j == 1) ? i : (j == 2) ? rows - 1 : i;
                int y = (j == 0) ? i : (j == 1) ? 0 : (j == 2) ? i : cols - 1;

                if (grid[y][x].isRoad()) {
                    Direction[] directions = grid[y][x].getRoad().getDirection();
                    Position pos = new Position(x, y);
                    boolean isValid = false;

                    for (Direction dir : directions) {
                        if (dir.move(pos).posIsInMap(rows, cols)) {
                            isValid = true;
                            break;
                        }
                    }

                    if (isValid) {
                        result.add(pos);
                    }
                }
            }
        }
        
        return result;
    }


    public static boolean detectDeadlock(CityMap map, Vehicle vehicle) {
        Set<Vehicle> visited = new HashSet<>();
        return dfsCycle(map, vehicle, visited, new HashSet<>());
    }

    private static boolean dfsCycle(CityMap map, Vehicle current, Set<Vehicle> visited, Set<Vehicle> recursionStack) {
        if (current == null) {
            
            return false;
        }

        if (recursionStack.contains(current)) {
            
            return true;
        }

        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        recursionStack.add(current);

        Position frontPos = current.getPosition().getNextPosition(current.getDirection());
        Vehicle frontVehicle = map.getVehicleAt(frontPos);

        

        if (dfsCycle(map, frontVehicle, visited, recursionStack)) {
            return true;
        }

        recursionStack.remove(current);
        return false;
    }



}