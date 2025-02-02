package Modele;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Image {

    static BufferedImage imgCar;
    static BufferedImage imgBuilding;
    static BufferedImage imgRoad;
    static BufferedImage imgStop;
    static BufferedImage imgTrafficLight;
    static BufferedImage imgNoEntry;



    
    public static void loadImg(){
        try {
            imgCar = ImageIO.read(new File("car.png"));
            imgBuilding = ImageIO.read(new File("building.png"));
            imgRoad = ImageIO.read(new File("road.png"));
            imgStop = ImageIO.read(new File("stop.png"));
            imgTrafficLight = ImageIO.read(new File("trafficlight.png"));
            imgNoEntry = ImageIO.read(new File("noentry.png"));
            
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des images");
        }
    }

    public static BufferedImage getCar(){
        return imgCar;
    }

    public static BufferedImage getBuilding(){
        return imgBuilding;
    }

    public static BufferedImage getRoad(){
        return imgRoad;
    }

    public static BufferedImage getStop(){
        return imgStop;
    }

    public static BufferedImage getTrafficLight(){
        return imgTrafficLight;
    }

    public static BufferedImage getNoEntry(){
        return imgNoEntry;
    }
    
}
