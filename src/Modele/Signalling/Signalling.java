package Modele.Signalling;

import java.awt.image.BufferedImage;
import Modele.Position;


public abstract class Signalling {
    protected Position position;
    
    public Signalling(Position position) {
        this.position = position;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public abstract String getType();

    public abstract BufferedImage getImg();
}


