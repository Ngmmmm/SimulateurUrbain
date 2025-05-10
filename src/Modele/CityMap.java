package Modele;

import Modele.Signalling.*;
import Modele.Vehicle.Vehicle;
import java.util.*;

public class CityMap {
    private int rows;
    private int cols;
    LinkedList<Position> accessiblePosition;
    private boolean isModefrequence = false;
    private Case[][] grid; // Grille représentant la carte

    public CityMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Case[rows][cols];
        initializeGrid();
    }

    // Initialisation de la grille avec des cellules vides
    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Case();
                grid[i][j].setPos(new Position(j, i));
            }
        }
    }

    public void setRoad(Position pos, String ligne, Direction... d) {
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col)) {
            Road currentRoad = grid[row][col].getRoad();
    
            if (currentRoad != null) {
                // Fusionner les directions existantes avec les nouvelles
                List<Direction> updatedDirections = new ArrayList<>(Arrays.asList(currentRoad.getDirection()));
                currentRoad.setIntersection(true);
                for (Direction newDirection : d) {
                    if (!updatedDirections.contains(newDirection)) {
                        updatedDirections.add(newDirection);
                    }
                }
                currentRoad.setDirection(updatedDirections.toArray(Direction[]::new));
                currentRoad.setDoubleLane("Pointillé".equals(ligne));
            } else {
                Road newRoad = new Road(d, ligne);
                newRoad.setDoubleLane("Pointillé".equals(ligne));
                grid[row][col].setRoad(newRoad);
            }
        }
    }

    public boolean isEdge(Position pos, Direction dir) {
        Position nextPos = pos.getNextPosition(dir);
        return nextPos == null || !isValidPosition(nextPos.getY(), nextPos.getX());
    }

    public void changeEdge(Position pos, Direction dir) {
        if (isEdge(pos, dir)) {
            Road currentRoad = getRoad(pos);
            if (currentRoad != null) {
                Direction newDirection = null;
                switch (dir.toString()) {
                    case "UP" -> newDirection = Direction.LEFT;
                    case "DOWN" -> newDirection = Direction.RIGHT;
                    case "RIGHT" -> newDirection = Direction.UP;
                    case "LEFT" -> newDirection = Direction.DOWN;
                }
                if (newDirection != null && !Arrays.asList(currentRoad.getDirection()).contains(newDirection)) {
                    currentRoad.addDirection(newDirection);
                }
            } else {
                System.out.println("Aucune route à la position " + pos + " pour changer l'edge.");
            }
        }
    }

    public void updateAllEdges() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(j, i);
                Road currentRoad = grid[i][j].getRoad();
                if (currentRoad != null) {
                    if (isEdge(pos, Direction.UP)) changeEdge(pos, Direction.UP);
                    if (isEdge(pos, Direction.DOWN)) changeEdge(pos, Direction.DOWN);
                    if (isEdge(pos, Direction.LEFT)) changeEdge(pos, Direction.LEFT);
                    if (isEdge(pos, Direction.RIGHT)) changeEdge(pos, Direction.RIGHT);
                }
            }
        }
    }

    public void addVehicle(Vehicle vehicle) {
        Position pos = vehicle.getPosition();
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col)) {
            grid[row][col].setVehicle(vehicle);
        }
    }

    public boolean isPositionOccupied(Position pos) {
        for (Vehicle v : getAllVehicles()) {  
            if (v.getPosition().equals(pos)) return true;
        }
        return false;
    }

    public void setStop(Position pos) {
        int row = pos.getY();
        int col = pos.getX();
        Direction dir = getRoad(pos).getDirection()[0];
        if (isValidPosition(row, col) && grid[row][col].getRoad() != null) {
            Stop stopSign = new Stop(pos, dir);
            grid[row][col].setSignalling(stopSign);
            grid[row][col].getRoad().setStopSign(true);
        } else {
            System.out.println("Impossible d'ajouter un Stop ici (position invalide ou pas de route).");
        }
    }

    public void addTrafficLight(TrafficLight trafficLight) {
        Position pos = trafficLight.getPosition();
        int row = pos.getY();
        int col = pos.getX();
        if (isValidPosition(row, col) && grid[row][col].getRoad() != null) {
            grid[row][col].setSignalling(trafficLight);
            grid[row][col].getRoad().setTrafficLight(true);
            
            if (grid[row][col].getRoad().getDirection().length > 1) {
                Position nextPos = grid[row][col].getRoad().getDirection()[1].move(pos);
                Road r = grid[nextPos.getY()][nextPos.getX()].getRoad();
                
                while (r != null && r.getDirection()[0] == grid[row][col].getRoad().getDirection()[0]) {
                    r.setTrafficLight(true);
                    nextPos = grid[row][col].getRoad().getDirection()[1].move(nextPos);
                    r = grid[nextPos.getY()][nextPos.getX()].getRoad();
                }
            }
        }
    }

    public void setNoEntry(Position position) {
        Road road = getRoad(position);
        if (road == null) return;

        Direction[] directions = road.getDirection();
        if (directions.length == 0) {
            System.out.println("Impossible d'inverser une direction inexistante !");
            return;
        }

        Direction oppositeDirection = directions[0].getOpposite();
        NoEntry noEntry = new NoEntry(position, oppositeDirection);

        grid[position.getY()][position.getX()].setSignalling(noEntry);
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void setBuilding() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].getRoad() == null) {
                    grid[i][j] = new Case("Building");
                }
            }
        }
    }

    public void findAccessiblePos(Position p) {
        accessiblePosition = Pathfinding.findAllAccessiblePositions(this, p);
    }

    public LinkedList<Position> getAccessiblePosition() {
        return accessiblePosition;
    }

    public Case[][] getGrid() {
        return grid;
    }

    public boolean isRoad(Position pos) {
        int row = pos.getY();
        int col = pos.getX();
        return isValidPosition(row, col) && grid[row][col].getRoad() != null;
    }

    public Road getRoad(Position pos) {
        int row = pos.getY();
        int col = pos.getX();
        return isValidPosition(row, col) ? grid[row][col].getRoad() : null;
    }

    public static Case getCase(Case[][] grid, Position pos) {
        if (pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < 50 && pos.getY() < 50) {
            return grid[pos.getY()][pos.getX()];
        }
        return null;
    }

    public int getRows() { return rows; }

    public int getCols() { return cols; }

    public void update() {} // pour remplir l'implémentation en attendant

    public boolean isCarOnPosition(Position pos) {
        return grid[pos.getY()][pos.getX()].getVehicle() != null;
    }

    public Vehicle getVehicleAt(Position pos) { 
        if (pos.posIsInMap(rows, cols)){
            return grid[pos.getY()][pos.getX()].getVehicle(); 
        }
        return null;
        
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Vehicle vehicle = grid[i][j].getVehicle();
                if (vehicle != null) vehicles.add(vehicle);
            }
        }
        return vehicles;
    }

    public boolean haveBuildingNext(Case eCase) {
        Position pos = eCase.getPos();
        for (Position adjacent : pos.posAutour4(this.cols, this.rows)) {
            Case adjacentCase = grid[adjacent.getY()][adjacent.getX()];
            if (adjacentCase != null && adjacentCase.getBuilding() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean getMode() { return isModefrequence; }

    public void setMode(boolean b) { isModefrequence = b; }
}
