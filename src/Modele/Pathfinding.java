package Modele;

import java.util.LinkedList;

public class Pathfinding {

    private Pathfinding() {
        // Empêcher l'instanciation de la classe statique
    }

    public static LinkedList<Position> findShortestPath(CityMap cityMap, Position start, Position goal) {
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
        return new LinkedList<>();
    }

    private static LinkedList<Position> reconstructPath(Position[][] previous, Position goal) {
        LinkedList<Position> path = new LinkedList<>();
        Position current = goal;

        while (current != null) {
            path.add(0, current);
            current = previous[current.getY()][current.getX()];
        }
        return path;
    }

    
    public static LinkedList<Position> findAllAccessiblePositions(CityMap cityMap, Position start) {
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
