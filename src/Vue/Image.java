package Vue;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Image {

    static BufferedImage imgCar;
    static BufferedImage imgBuilding;
    static BufferedImage imgRoad;
    static BufferedImage imgStop;
    static BufferedImage imgTrafficGreen;
    static BufferedImage imgTrafficRed;
    static BufferedImage imgTrafficOrange;
    static BufferedImage imgNoEntry;
    static BufferedImage imgVirage;




    
    public static void loadImg(){
        try {
            imgCar = ImageIO.read(new File("car.png"));
            imgBuilding = ImageIO.read(new File("building.png"));
            imgRoad = ImageIO.read(new File("road.png"));
            imgStop = ImageIO.read(new File("stop.png"));
            imgTrafficGreen = ImageIO.read(new File("trafficGreen.png"));
            imgTrafficRed = ImageIO.read(new File("trafficRed.png"));
            imgTrafficOrange = ImageIO.read(new File("trafficOrange.png"));
            imgNoEntry = ImageIO.read(new File("noentry.png"));
            imgVirage = ImageIO.read(new File("virage.png"));
            
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des images");
        }
    }

    
}
