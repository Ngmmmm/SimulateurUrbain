import Modele.Direction;
import Modele.Position;
import Modele.Signalling.TrafficLight;
import Modele.Signalling.TrafficLightGroup;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        
        List<TrafficLight> allLights = new ArrayList<>();
        allLights.add(new TrafficLight(new Position(2, 3), Direction.UP, null)  );
        allLights.add(new TrafficLight(new Position(3, 3), Direction.LEFT, null)   );


        

        TrafficLightGroup groupe1 = new TrafficLightGroup(allLights);
    
    }
    
}
