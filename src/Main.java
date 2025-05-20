import javax.swing.SwingUtilities;

import Controle.Controle;

public class Main {

    // --- Point d'entrée ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Controle::new);
    }
    
}
