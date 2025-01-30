package Modele;

public class Road {
    private Direction direction;
    private String lineType;

    public Road(Direction direction, String lineType) {
        this.direction = direction;
        this.lineType = lineType;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }
}
