package Modele.Signalling;

import Modele.Position;

class NoEntry extends Signalling {
    
    public NoEntry(Position position) {
        super(position);
    }
    
    @Override
    public String getType() {
        return "NoEntry";
    }

}
