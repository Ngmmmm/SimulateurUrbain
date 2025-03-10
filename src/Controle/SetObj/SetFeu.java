package Controle.SetObj;

import Modele.Stack2;
import Modele.CityMap;
import Modele.Position;
import Modele.Case;

public class SetFeu implements Update {
    
    public static final SetFeu set = new SetFeu();

    public void update(CityMap map, Stack2 c){

    }
    
    @Override
    public String toString(){
        return "feu";
    }
}
