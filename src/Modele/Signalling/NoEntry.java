package Modele.Signalling;

import Modele.*;

public class NoEntry extends Signalling {
    
    public NoEntry(Position position, Direction direction) {
        super(position, direction);
    }
    
    @Override
    public String getType() {
        return "NoEntry";
    }

}
