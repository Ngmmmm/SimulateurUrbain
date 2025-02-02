package Modele.Signalling;

import java.awt.image.BufferedImage;
import Modele.Position;
import Modele.Image;

class Stop extends Signalling {
    
    public Stop(Position position) {
        super(position);
    }
    
    @Override
    public String getType() {
        return "Stop";
    }

    public BufferedImage getImg(){
        return Image.getStop();
    }
}
