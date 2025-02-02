package Modele.Signalling;

import Modele.Position;

class TrafficLights extends Signalling {
    private enum LightColor {RED, ORANGE, GREEN}
    private LightColor color;
    
    public TrafficLights(Position position) {
        super(position);
        this.color = LightColor.RED;
    }
    
    public String getColor() {
        return color.name();
    }
    
    public void setColor(String color) {
        try {
            this.color = LightColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color.");
        }
    }
    
    public void switchColor() {
        switch (color) {
            case RED    -> color = LightColor.GREEN;
            case ORANGE -> color = LightColor.RED;
            case GREEN  -> color = LightColor.ORANGE;
        }
    }
    
    @Override
    public String getType() {
        return "TrafficLights";
    }


}
