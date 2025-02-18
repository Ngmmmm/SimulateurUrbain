package Modele.Signalling;

import Modele.Direction;
import Modele.Position;

public class Stop extends Signalling {
    
    public Stop(Position position, Direction direction) {
        super(position, direction);
    }
    
    @Override
    public String getType() {
        return "Stop";
    }

}
