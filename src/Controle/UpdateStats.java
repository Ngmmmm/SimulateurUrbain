package Controle;

import Modele.Statistique;
import javax.swing.*;
import java.awt.Component;

public class UpdateStats {

    public static void update(Component[] comps, Statistique stat) {
        for (Component c : comps) {
            if (c instanceof JLabel label) {
                String text = label.getText();

                if (text.contains("trajet")) {
                    label.setText("Temps de trajet moyen: " + stat.getTemps() + "u");
                } else if (text.contains("Embouteillages")) {
                    label.setText("Embouteillages: " + stat.getEmbouteillage() + "%");
                } else if (text.contains("Nombre")) {
                    label.setText("Nombre de Voiture: " + stat.getVoiture());
                } else if (text.contains("arret")) {
                    label.setText("Temps à l'arrêt moyen: " + stat.getArret() + "u");
                }
            }
        }
    }
}
