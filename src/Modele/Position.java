package Modele;

import java.util.*;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {}

    public Position checkPosForSignalling(Direction dir) {
        switch (dir.toString()) {
            case "UP": return new Position(this.x + 1, this.y - 1);
            case "LEFT": return new Position(this.x - 1, this.y - 1);
            case "RIGHT": return new Position(this.x + 1, this.y + 1);
            case "DOWN": return new Position(this.x - 1, this.y + 1);
            default: return null;
        }
    }

    public Position getNextPosition(Direction dir) {
        switch (dir.toString()) {
            case "UP": return new Position(this.x, this.y - 1);
            case "DOWN": return new Position(this.x, this.y + 1);
            case "LEFT": return new Position(this.x - 1, this.y);
            case "RIGHT": return new Position(this.x + 1, this.y);
            default: return null;
        }
    }

    public ArrayList<Position> posAutour4(int gridWidth, int gridHeight) {
        ArrayList<Position> positions = new ArrayList<>();
        Position[] adjacentes = {
            new Position(this.x, this.y - 1), // Haut
            new Position(this.x, this.y + 1), // Bas
            new Position(this.x - 1, this.y), // Gauche
            new Position(this.x + 1, this.y)  // Droite
        };

        for (Position p : adjacentes) {
            if (p.posIsInMap(gridWidth, gridHeight)) positions.add(p);
        }
        return positions;
    }

    public ArrayList<Position> posAutour8(int gridWidth, int gridHeight) {
        ArrayList<Position> positions = posAutour4(gridWidth, gridHeight);
        Position[] diagonales = {
            new Position(this.x - 1, this.y - 1), // Haut gauche
            new Position(this.x + 1, this.y - 1), // Haut droit
            new Position(this.x - 1, this.y + 1), // Bas gauche
            new Position(this.x + 1, this.y + 1)  // Bas droit
        };

        for (Position p : diagonales) {
            if (p.posIsInMap(gridWidth, gridHeight)) positions.add(p);
        }
        return positions;
    }

    public boolean posIsInMap(int gridWidth, int gridHeight) {
        return (this.x >= 0 && this.y >= 0 && this.x < gridWidth && this.y < gridHeight);
    }

    public static Position genRandomPos(int gridWidth, int gridHeight) {
        Random rand = new Random();
        return new Position(rand.nextInt(gridWidth), rand.nextInt(gridHeight));
    }

    public double distanceTo(Position other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    // Getters & Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
