package Vue;

import Modele.Direction;
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
    static BufferedImage imgCarUp;
    static BufferedImage imgCarDown;
    static BufferedImage imgCarLeft;
    static BufferedImage imgCarRight;
    static BufferedImage imgMaison1;
    static BufferedImage imgMaison2;
    static BufferedImage imgMaison3;





    
    public static void loadImg(){
        try {
            imgCar = ImageIO.read(new File("car.png"));
            imgBuilding = ImageIO.read(new File("maison1.png"));
            imgRoad = ImageIO.read(new File("road.png"));
            imgStop = ImageIO.read(new File("stop.png"));
            imgTrafficGreen = ImageIO.read(new File("trafficGreen.png"));
            imgTrafficRed = ImageIO.read(new File("trafficRed.png"));
            imgTrafficOrange = ImageIO.read(new File("trafficOrange.png"));
            imgNoEntry = ImageIO.read(new File("noentry.png"));
            imgVirage = ImageIO.read(new File("virage.png"));
            imgCarUp = ImageIO.read(new File("carUp.png"));
            imgCarDown = ImageIO.read(new File("carDown.png"));
            imgCarLeft = ImageIO.read(new File("carLeft.png"));
            imgCarRight = ImageIO.read(new File("carRight.png"));
            imgMaison1 = ImageIO.read(new File("maison1.png"));
            imgMaison2 = ImageIO.read(new File("maison2.png"));
            imgMaison3 = ImageIO.read(new File("maison3.png"));
            
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des images");
        }
    }

    public static BufferedImage getImgCar(Direction dir){
        return switch (dir.toString()) {
            case "UP" -> imgCarUp;
            case "DOWN" -> imgCarDown;
            case "LEFT" -> imgCarLeft;
            case "RIGHT" -> imgCarRight;
            default -> null;
        };
    }

    public static BufferedImage getRandomBuilding(){
        int random = (int)(Math.random() * 3);
        return switch (random) {
            case 0 -> imgMaison1;
            case 1 -> imgMaison2;
            case 2 -> imgMaison3;
            default -> null;
        };
    }

    
}
