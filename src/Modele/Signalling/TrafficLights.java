package Modele.Signalling;

import javax.swing.text.Position;

public class TrafficLights extends Signalling{

    String color;

    public TrafficLights(Position pos){
        super(pos);
        color = "Red";
    }


    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void switchColor(){
        if(color != null){
            switch (color) {
                case "Red" -> color = "Green";
                case "Orange" -> color = "Red";
                case "Green" -> color = "Orange";
                default -> {
                }
            }
        }
    }

}