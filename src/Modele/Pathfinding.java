package Modele;

import java.util.ArrayList;

public class Pathfinding {

    private Pathfinding() {
        // Empêcher l'instanciation de la classe statique
    }

    public static ArrayList<Position> findShortestPath(CityMap cityMap, Position start, Position goal) {
        int rows = cityMap.getGrid().length;
        int cols = cityMap.getGrid()[0].length;
    
        boolean[][] visited = new boolean[rows][cols]; // Marqueur des cases visitées
        double[][] distances = new double[rows][cols]; // Distance minimale trouvée
        Position[][] previous = new Position[rows][cols]; // Pour reconstituer le chemin
    
        // Initialisation
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                distances[i][j] = Double.MAX_VALUE;
            }
        }
        distances[start.getY()][start.getX()] = 0;
    
        // Tas min pour les positions à explorer
        MinHeap openSet = new MinHeap(distances);
        openSet.insert(start);
    
        while (!openSet.isEmpty()) {
            // Retirer la position avec la plus petite distance
            Position current = openSet.extractMin();
    
            // Si on atteint la destination
            if (current.equals(goal)) {
                return reconstructPath(previous, goal);
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
                        previous[neighbor.getY()][neighbor.getX()] = current;
                        openSet.insert(neighbor);
                    }
                }
            }
        }
    
        // Aucun chemin trouvé
        return new ArrayList<>();
    }

    private static ArrayList<Position> reconstructPath(Position[][] previous, Position goal) {
        ArrayList<Position> path = new ArrayList<>();
        Position current = goal;

        while (current != null) {
            path.add(0, current);
            current = previous[current.getY()][current.getX()];
        }
        return path;
    }

    
    public static ArrayList<Position> findAllAccessiblePositions(CityMap cityMap, Position start) {
        int rows = cityMap.getGrid().length;
        int cols = cityMap.getGrid()[0].length;
    
        boolean[][] visited = new boolean[rows][cols]; // Marqueur des cases visitées
        double[][] distances = new double[rows][cols]; // Distance minimale trouvée
    
        // Initialisation
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                distances[i][j] = Double.MAX_VALUE;
            }
        }
        distances[start.getY()][start.getX()] = 0;
    
        // Tas min pour les positions à explorer
        MinHeap openSet = new MinHeap(distances);
        openSet.insert(start);
    
        while (!openSet.isEmpty()) {
            // Retirer la position avec la plus petite distance
            Position current = openSet.extractMin();
    
            // Marquer la position comme visitée
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
                        openSet.insert(neighbor);
                    }
                }
            }
        }
    
        // Collecter toutes les positions accessibles
        ArrayList<Position> accessiblePositions = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (distances[i][j] != Double.MAX_VALUE) {
                    accessiblePositions.add(new Position(j, i)); // (x, y)
                }
            }
        }
    
        return accessiblePositions;
    }
}
