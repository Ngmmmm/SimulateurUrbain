package Modele;

public enum Direction {
    UP(0, -1),    // ordinal() = 0
    DOWN(0, 1),   // ordinal() = 1
    LEFT(-1, 0),  // ordinal() = 2
    RIGHT(1, 0);   // ordinal() = 3

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public Position move(Position position) {
        return new Position(position.getX() + deltaX, position.getY() + deltaY);
    }

    @Override
    public String toString() {
        return name(); // Utilise le nom de l'enum (UP, DOWN, etc.)
    }

    public static Direction fromString(String direction) {
        try {
            return valueOf(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Direction inconnue: " + direction);
        }
    }

    public static Direction getDirection(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        if (dx == 1 && dy == 0) return RIGHT;
        if (dx == -1 && dy == 0) return LEFT;
        if (dx == 0 && dy == 1) return DOWN;
        if (dx == 0 && dy == -1) return UP;

        return null;
    }

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return null;
        }
    }
}