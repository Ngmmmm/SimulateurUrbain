package Vue;

import Modele.Direction;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Image {

    static BufferedImage imgCar;
    static BufferedImage imgCamionUp;
    static BufferedImage imgBusUp;
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
    static BufferedImage intersection;
    static BufferedImage imgCamionLeft;
    static BufferedImage imgCamionRight;
    static BufferedImage imgCamionDown;
    static BufferedImage imgBusDown;
    static BufferedImage imgBusRight;
    static BufferedImage imgBusLeft;




    
    public static void loadImg(){
        try {
            imgCar = ImageIO.read(new File("img/car.png"));
            imgBuilding = ImageIO.read(new File("img/maison1.png"));
            imgRoad = ImageIO.read(new File("img/road.png"));
            imgStop = ImageIO.read(new File("img/stop.png"));
            imgTrafficGreen = ImageIO.read(new File("img/trafficGreen.png"));
            imgTrafficRed = ImageIO.read(new File("img/trafficRed.png"));
            imgTrafficOrange = ImageIO.read(new File("img/trafficOrange.png"));
            imgNoEntry = ImageIO.read(new File("img/noentry.png"));
            imgVirage = ImageIO.read(new File("img/virage.png"));
            imgCarUp = ImageIO.read(new File("img/carUp.png"));
            imgCarDown = ImageIO.read(new File("img/carDown.png"));
            imgCarLeft = ImageIO.read(new File("img/carLeft.png"));
            imgCarRight = ImageIO.read(new File("img/carRight.png"));
            imgMaison1 = ImageIO.read(new File("img/maison1.png"));
            imgMaison2 = ImageIO.read(new File("img/maison2.png"));
            imgMaison3 = ImageIO.read(new File("img/maison3.png"));
            intersection = ImageIO.read(new File("img/intersection.png"));
            imgBusUp = ImageIO.read(new File("img/busUp.png"));
            imgCamionUp = ImageIO.read(new File("img/camionUp.PNG"));
            imgBusDown = ImageIO.read(new File("img/busDown.PNG"));
            imgBusLeft = ImageIO.read(new File("img/busLeft.PNG"));
            imgBusRight = ImageIO.read(new File("img/busRight.PNG"));
            imgCamionDown = ImageIO.read(new File("img/camionDown.PNG"));
            imgCamionLeft = ImageIO.read(new File("img/camionLeft.PNG"));
            imgCamionRight = ImageIO.read(new File("img/camionRight.PNG"));

            
        } catch (IOException e) {
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

    public static BufferedImage getImgBus(Direction dir){
        return switch (dir.toString()) {
            case "UP" -> imgBusUp;
            case "DOWN" -> imgBusDown;
            case "LEFT" -> imgBusLeft;
            case "RIGHT" -> imgBusRight;
            default -> null;
        };
    }

    public static BufferedImage getImgCamion(Direction dir){
        return switch (dir.toString()) {
            case "UP" -> imgCamionUp;
            case "DOWN" -> imgCamionDown;
            case "LEFT" -> imgCamionLeft;
            case "RIGHT" -> imgCamionRight;
            default -> null;
        };
    }

    

    
}
