package Modele.Signalling;

import Controle.Controle;
import Modele.*;

public class TrafficLight extends Signalling {
    
    private LightColor color;
    private int idGroupe;

    public TrafficLight(Position pos, Direction direction, CityMap map) {
        super(pos, direction);
        this.color = LightColor.RED; // Initialisé en rouge
        if (map != null) map.addTrafficLight(this);
    }

    public boolean isNorthSouth() {
        return direction == Direction.UP || direction == Direction.DOWN;
    }

    public void setColor(LightColor color, Controle ctrl) {
        if (color != null){
            this.color = color;
            ctrl.addUpdate();
        }
    }

    @Override
    public Direction getDirection() { return direction; }

    @Override
    public Position getPosition() { return position; }

    @Override
    public String getType() { return "TrafficLights"; }

    @Override
    public String toString() {
        return "Feu à (" + position.getX() + "," + position.getY() + ") [" + direction + "] -> " + color;
    }

    // --- Getters / Setters en bas ---

    public String getColor() { return color.name(); }

    public LightColor getCol() { return color; }

    public int getIDGroupe() { return idGroupe; }

    public void setIDGroupe(int id) { this.idGroupe = id; }

    public TrafficLightGroup getTrafficLightGroup(Controle ctrl){
        if (ctrl == null) return null;
        for (TrafficLightGroup g : ctrl.getLights()) {
            if (g.getId() == idGroupe) return g;
        }
        return null;
    }
}
