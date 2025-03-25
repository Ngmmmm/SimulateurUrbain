package Controle;

import java.awt.Component;
import javax.swing.*;

import Modele.Statistique;


public class UpdateStats {
    
    public static void update(Component[] comps, Statistique stat){
        for(Component c : comps){
            if (c instanceof JLabel){
                JLabel label = (JLabel) c;
                if (label.getText().contains("Temps")){
                    label.setText("Temps de trajet moyen: " + stat.getTemps() + "u");
                }
                if (label.getText().contains("Embouteillages")){
                    label.setText("Embouteillages: " + stat.getEmbouteillage() + "%");
                }
                if(label.getText().contains("Nombre")){
                    label.setText("Nombre de Voiture: " + stat.getVoiture());
                }
            }
        }
    }
}
