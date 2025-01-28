package Vue;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Image {

    static BufferedImage imgCar;
    static BufferedImage imgBus;
    static BufferedImage imgTruck;
    static BufferedImage imgMoto;
    static BufferedImage imgBuilding;
    static BufferedImage imgRoad;
    static BufferedImage imgStop;
    static BufferedImage imgTrafficLight;



    
    public static void loadImg(){
        try {
            imgCar = ImageIO.read(new File("test"));
            imgBus = ImageIO.read(new File("test"));
            imgTruck = ImageIO.read(new File("test"));
            imgMoto = ImageIO.read(new File("test"));
            imgBuilding = ImageIO.read(new File("test"));
            imgRoad = ImageIO.read(new File("test"));
            imgStop = ImageIO.read(new File("test"));
            imgTrafficLight = ImageIO.read(new File("test"));
            
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des images");
        }
    }
    
}
