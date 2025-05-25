package Modele;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MatrixGenerator {

    public static void main(CityMap cityMap) {
        generateAdjacencyMatrix(cityMap);
        generateIncidenceMatrix(cityMap);
        generateDistanceMatrix(cityMap);
    }

    public static void generateAdjacencyMatrix(CityMap cityMap) {
        int size = cityMap.getRows() * cityMap.getCols();
        int[][] adjacencyMatrix = new int[size][size];

        for (int i = 0; i < cityMap.getRows(); i++) {
            for (int j = 0; j < cityMap.getCols(); j++) {
                Case currentCase = cityMap.getGrid()[i][j];
                if (currentCase.getRoad() != null) {
                    for (Direction dir : currentCase.getRoad().getDirection()) {
                        Position nextPos = dir.move(currentCase.getPos());
                        if (nextPos != null && cityMap.isValidPosition(nextPos.getY(), nextPos.getX())) {
                            int currentIndex = i * cityMap.getCols() + j;
                            int nextIndex = nextPos.getY() * cityMap.getCols() + nextPos.getX();
                            adjacencyMatrix[currentIndex][nextIndex] = 1;
                        }
                    }
                }
            }
        }

        writeMatrixToFile(adjacencyMatrix, "adjacency_matrix.txt");
    }

    public static void generateIncidenceMatrix(CityMap cityMap) {
        int numNodes = cityMap.getRows() * cityMap.getCols();
        List<Road> roads = new ArrayList<>();
        for (int i = 0; i < cityMap.getRows(); i++) {
            for (int j = 0; j < cityMap.getCols(); j++) {
                Case currentCase = cityMap.getGrid()[i][j];
                if (currentCase.getRoad() != null) {
                    roads.add(currentCase.getRoad());
                }
            }
        }
        int numRoads = roads.size();
        int[][] incidenceMatrix = new int[numNodes][numRoads];

        for (int i = 0; i < cityMap.getRows(); i++) {
            for (int j = 0; j < cityMap.getCols(); j++) {
                Case currentCase = cityMap.getGrid()[i][j];
                if (currentCase.getRoad() != null) {
                    int nodeIndex = i * cityMap.getCols() + j;
                    int roadIndex = roads.indexOf(currentCase.getRoad());
                    incidenceMatrix[nodeIndex][roadIndex] = 1;
                }
            }
        }

        writeMatrixToFile(incidenceMatrix, "incidence_matrix.txt");
    }

    public static void generateDistanceMatrix(CityMap cityMap) {
        int size = cityMap.getRows() * cityMap.getCols();
        int[][] distanceMatrix = new int[size][size];

        for (int i = 0; i < cityMap.getRows(); i++) {
            for (int j = 0; j < cityMap.getCols(); j++) {
                Case currentCase = cityMap.getGrid()[i][j];
                if (currentCase.getRoad() != null) {
                    for (Direction dir : currentCase.getRoad().getDirection()) {
                        Position nextPos = dir.move(currentCase.getPos());
                        if (nextPos != null && cityMap.isValidPosition(nextPos.getY(), nextPos.getX())) {
                            int currentIndex = i * cityMap.getCols() + j;
                            int nextIndex = nextPos.getY() * cityMap.getCols() + nextPos.getX();
                            distanceMatrix[currentIndex][nextIndex] = 1;
                        }
                    }
                }
            }
        }

        // Utiliser l'algorithme de Floyd-Warshall pour calculer les distances les plus courtes
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (distanceMatrix[i][k] != 0 && distanceMatrix[k][j] != 0) {
                        if (distanceMatrix[i][j] == 0 || distanceMatrix[i][j] > distanceMatrix[i][k] + distanceMatrix[k][j]) {
                            distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                        }
                    }
                }
            }
        }

        writeMatrixToFile(distanceMatrix, "distance_matrix.txt");
    }

    public static void writeMatrixToFile(int[][] matrix, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int[] row : matrix) {
                for (int val : row) {
                    writer.write(val + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
