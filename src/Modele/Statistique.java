package Modele;

public class Statistique {
    int nbVoiture = 0;
    int hasMoved = 0;
    int tempsMoyen = 0;
    int tempsEchantillon = 0;


    public void addVoiture(){
        nbVoiture++;
    }

    public void delVoiture(int temps){
        nbVoiture--;
        tempsMoyen += temps;
        tempsEchantillon++;
    }

    public void isMoving(){
        hasMoved++;
    }

    public void resetMoved(){
        hasMoved = 0;
    }

    public int getVoiture(){
        return nbVoiture;
    }


    public int getTemps(){
        return (tempsEchantillon != 0)?tempsMoyen/tempsEchantillon : 0;
    }

    public String getEmbouteillage(){
        System.out.println(hasMoved);
        return (nbVoiture != 0)? String.valueOf((nbVoiture - hasMoved)*100 / nbVoiture) : "0";
    }
}
