package Controle;

import Modele.Statistique;
import java.awt.Component;
import javax.swing.*;


public class UpdateStats {
    
    public static void update(Component[] comps, Statistique stat){
        for(Component c : comps){
            if (c instanceof JLabel label){
                if (label.getText().contains("trajet")){
                    label.setText("Temps de trajet moyen: " + stat.getTemps() + "u");
                }
                if (label.getText().contains("Embouteillages")){
                    label.setText("Embouteillages: " + stat.getEmbouteillage() + "%");
                }
                if(label.getText().contains("Nombre")){
                    label.setText("Nombre de Voiture: " + stat.getVoiture());
                }
                if(label.getText().contains("arret")){
                    label.setText("Temps à l'arret moyen: " + stat.getArret() + "u");
                }
            }
        }
    }
}
