package Modele.Signalling;

import Modele.*;

public class Stop extends Signalling {
    
    public Stop(Position position, Direction direction) {
        super(position, direction);
    }
    
    @Override
    public String getType() {
        return "Stop";
    }

}
