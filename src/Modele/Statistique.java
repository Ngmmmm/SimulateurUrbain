package Modele;

public class Statistique {
    private int nbVoiture = 0;
    private int hasMoved = 0;
    private int tempsMoyen = 0;
    private int tempsEchantillon = 0;
    private int tempsArret = 0;
    private int nbArret = 0;
    private int maxPassage = 0;

    // Ajoute une voiture et augmente le compteur des voitures
    public void addVoiture() {
        nbVoiture++;
    }

    // Retire une voiture, met à jour les temps de passage, et calcule la moyenne des temps
    public void delVoiture(int temps) {
        if (nbVoiture > 0) {
            nbVoiture--;
            tempsMoyen += temps;
            tempsEchantillon++;
        }
    }

    // Compte le nombre de véhicules en mouvement
    public void isMoving() {
        hasMoved++;
    }

    // Réinitialise le nombre de véhicules en mouvement
    public void resetMoved() {
        hasMoved = 0;
    }

    // Retourne le nombre actuel de véhicules
    public int getVoiture() {
        return nbVoiture;
    }

    // Enregistre un arrêt avec le temps d'arrêt
    public void addArret(int temps) {
        if (temps > 0) {
            tempsArret += temps;
            nbArret++;
        }
    }

    // Retourne la moyenne des temps de passage des véhicules
    public int getTemps() {
        return (tempsEchantillon > 0) ? tempsMoyen / tempsEchantillon : 0;
    }

    // Retourne le pourcentage d'embouteillage en fonction des véhicules arrêtés
    public String getEmbouteillage() {
        return (nbVoiture > 0) ? String.valueOf((nbVoiture - hasMoved) * 100 / nbVoiture) : "0";
    }

    // Retourne le temps moyen d'arrêt
    public String getArret() {
        return (nbArret > 0) ? String.valueOf(tempsArret / nbArret) : "0";
    }

    // Met à jour le passage maximal sur une case
    public void updateMax(Case c) {
        if (c != null && c.getNbPassage() > maxPassage) {
            maxPassage = c.getNbPassage();
        }
    }

    // Retourne le maximum des passages sur une case
    public int getMaxPassage() {
        return (maxPassage > 0) ? maxPassage : 1; // Evite de renvoyer 0
    }
}
