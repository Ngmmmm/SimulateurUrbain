package Modele;


public class Direction {
    public static final Direction UP = new Direction(0, -1);
    public static final Direction DOWN = new Direction(0, 1);
    public static final Direction LEFT = new Direction(-1, 0);
    public static final Direction RIGHT = new Direction(1, 0);

    private final int deltaX;
    private final int deltaY;

    private Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public Position move(Position position) {
        return new Position(position.getX() + deltaX, position.getY() + deltaY);
    }

    @Override
    public String toString() {
        if (this == UP) return "UP";
        if (this == DOWN) return "DOWN";
        if (this == LEFT) return "LEFT";
        if (this == RIGHT) return "RIGHT";
        return "Unknown Direction";
    }
    // Trouve la direction entre deux positions
    public static Direction getDirection(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        if (dx == 1 && dy == 0) return Direction.RIGHT;
        if (dx == -1 && dy == 0) return Direction.LEFT;
        if (dx == 0 && dy == 1) return Direction.DOWN;
        if (dx == 0 && dy == -1) return Direction.UP;

        return null;
    }

    public Direction getOpposite() {
        if (this == UP) return DOWN;
        if (this == DOWN) return UP;
        if (this == LEFT) return RIGHT;
        if (this == RIGHT) return LEFT;
        return null;
    }


}
