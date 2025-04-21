package Modele;

public class Statistique {
    int nbVoiture = 0;
    int hasMoved = 0;
    int tempsMoyen = 0;
    int tempsEchantillon = 0;
    int tempsArret = 0;
    int nbArret = 0;
    int maxPassage = 0;


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

    public void addArret(int temps){
        if (temps != 0){
            tempsArret += temps;
            nbArret++;
        }
    }


    public int getTemps(){
        return (tempsEchantillon != 0)?tempsMoyen/tempsEchantillon : 0;
    }

    public String getEmbouteillage(){
        return (nbVoiture != 0)? String.valueOf((nbVoiture - hasMoved)*100 / nbVoiture) : "0";
    }

    public String getArret(){
        return (nbArret != 0)? String.valueOf(tempsArret / nbArret) : "0";
    }

    public void updateMax(Case c){
        if (c != null){
            if (c.getNbPassage() > maxPassage){
                maxPassage = c.getNbPassage();
            }
        }}
    

    public int getMaxPassage(){
        return (maxPassage == 0)? 1 : maxPassage;
    }


}
