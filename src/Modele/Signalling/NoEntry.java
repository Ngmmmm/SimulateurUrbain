package Modele.Signalling;

import java.awt.image.BufferedImage;
import Modele.Position;
import Modele.Image;

class NoEntry extends Signalling {
    
    public NoEntry(Position position) {
        super(position);
    }
    
    @Override
    public String getType() {
        return "NoEntry";
    }

    public BufferedImage getImg(){
        return Image.getNoEntry();
    }
}
