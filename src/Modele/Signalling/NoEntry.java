package Modele.Signalling;

import Modele.Direction;
import Modele.Position;

public class NoEntry extends Signalling {
    
    public NoEntry(Position position, Direction direction) {
        super(position, direction);
    }
    
    @Override
    public String getType() {
        return "NoEntry";
    }

}
