package Modele.Signalling;

import Modele.Position;

public class Stop extends Signalling {
    
    public Stop(Position position) {
        super(position);
    }
    
    @Override
    public String getType() {
        return "Stop";
    }

}
