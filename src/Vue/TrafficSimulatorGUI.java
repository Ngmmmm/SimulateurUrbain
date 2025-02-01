package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TrafficSimulatorGUI extends JFrame {

    // Taille de la carte
    private static final int ROWS = 20; // Nombre de lignes
    private static final int COLS = 20; // Nombre de colonnes
    private JPanel mapPanel; // Panneau de la carte
    private JScrollPane scrollPane; // Conteneur avec barre de défilement
    private int cellSize = 40; // Taille des cases (modifiable pour zoomer/dézoomer)
    private double zoomFactor = 1.0; // Facteur de zoom

    // Position initiale de la voiture
    private int carX = 10; // Position en X (au centre)
    private int carY = 10; // Position en Y (au centre)

    // Timer pour déplacer la voiture
    private Timer moveCarTimer;

    public TrafficSimulatorGUI() {
        setTitle("Simulateur de gestion de trafic urbain");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Ajouter le menu
        setJMenuBar(createMenuBar());
        setLocationRelativeTo(null);

        // Ajouter la carte
        JPanel containerPanel = new JPanel(new GridBagLayout()); // Conteneur centré
        mapPanel = createMapPanel();
        containerPanel.add(mapPanel, new GridBagConstraints()); // Centrage automatique
        scrollPane = new JScrollPane(containerPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600)); // Taille par défaut
        add(scrollPane, BorderLayout.CENTER);

        // Ajouter la barre des statistiques
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.SOUTH);

        // Ajouter l'écouteur pour zoomer avec la molette/trackpad
        mapPanel.addMouseWheelListener(createMouseWheelZoomListener());

        // Démarrer la simulation de déplacement de la voiture
        startCarMovement();

        setVisible(true);
    }

    // Démarrer le déplacement de la voiture
    private void startCarMovement() {
        // Créer un timer qui met à jour la position de la voiture toutes les 500ms
        moveCarTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCarRandomly();
                mapPanel.repaint(); // Redessiner la carte après chaque déplacement
            }
        });
        moveCarTimer.start();
    }

    // Déplacer la voiture dans une direction aléatoire
    private void moveCarRandomly() {
        Random random = new Random();
        int direction = random.nextInt(4); // Génère un nombre entre 0 et 3

        switch (direction) {
            case 0: // Gauche
                if (carX > 0) carX--;
                break;
            case 1: // Droite
                if (carX < COLS - 1) carX++;
                break;
            case 2: // Haut
                if (carY > 0) carY--;
                break;
            case 3: // Bas
                if (carY < ROWS - 1) carY++;
                break;
        }
    }

    // Création de la barre de menu
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem saveItem = new JMenuItem("Sauvegarder");
        JMenuItem loadItem = new JMenuItem("Charger");
        JMenuItem exitItem = new JMenuItem("Quitter");

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Menu Simulation
        JMenu simulationMenu = new JMenu("Simulation");
        JMenuItem startItem = new JMenuItem("Démarrer");
        JMenuItem stopItem = new JMenuItem("Arrêter");

        simulationMenu.add(startItem);
        simulationMenu.add(stopItem);
        menuBar.add(simulationMenu);

        // Menu Affichage (pour zoom)
        JMenu viewMenu = new JMenu("Affichage");
        JMenuItem zoomInItem = new JMenuItem("Zoom avant");
        JMenuItem zoomOutItem = new JMenuItem("Zoom arrière");

        zoomInItem.addActionListener(e -> zoom(true, null)); // Zoom avant
        zoomOutItem.addActionListener(e -> zoom(false, null)); // Zoom arrière

        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        menuBar.add(viewMenu);

        return menuBar;
    }

    // Création du panneau de la carte
    private JPanel createMapPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g); // Redessiner la carte
            }
        };
        panel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize)); // Taille initiale
        return panel;
    }

    // Redessiner la carte
    private void drawMap(Graphics g) {
        // Dessiner les cases
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = j * cellSize;
                int y = i * cellSize;

                g.setColor(Color.LIGHT_GRAY); // Couleur par défaut
                g.fillRect(x, y, cellSize, cellSize);

                g.setColor(Color.GRAY); // Bordure des cases
                g.drawRect(x, y, cellSize, cellSize);
            }
        }

        // Dessiner la voiture (un carré rouge)
        int carXPos = carX * cellSize;
        int carYPos = carY * cellSize;
        g.setColor(Color.RED); // Couleur de la voiture
        g.fillRect(carXPos, carYPos, cellSize, cellSize); // Dessiner le carré rouge
    }

    // Gestion du zoom (true pour zoom avant, false pour zoom arrière)
    private void zoom(boolean zoomIn, Point cursorPosition) {
        if (zoomIn) {
            zoomFactor += 0.1; // Augmente le facteur de zoom
        } else if (zoomFactor > 0.2) { // Empêche le zoom arrière excessif
            zoomFactor -= 0.1;
        }

        // Mettre à jour la taille des cases et la carte
        cellSize = (int) (40 * zoomFactor);
        mapPanel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
        mapPanel.revalidate(); // Ajuste la taille du panneau

        // Centrer la carte autour de la position du curseur
        if (cursorPosition != null) {
            JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();

            int newCenterX = (int) (cursorPosition.x * zoomFactor);
            int newCenterY = (int) (cursorPosition.y * zoomFactor);

            horizontal.setValue(Math.max(newCenterX - scrollPane.getWidth() / 2, 0));
            vertical.setValue(Math.max(newCenterY - scrollPane.getHeight() / 2, 0));
        }

        mapPanel.repaint(); // Redessine la carte
    }

    // Création d'un écouteur pour zoomer avec la molette/trackpad
    private MouseWheelListener createMouseWheelZoomListener() {
        return new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Point cursorPosition = e.getPoint(); // Position du curseur
                if (e.getPreciseWheelRotation() < 0) {
                    zoom(true, cursorPosition); // Zoom avant
                } else {
                    zoom(false, cursorPosition); // Zoom arrière
                }
            }
        };
    }

    // Création de la barre des statistiques
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));

        JLabel avgTimeLabel = new JLabel("Temps moyen: 0s");
        JLabel congestionLabel = new JLabel("Embouteillages: 0%");
        JLabel fuelLabel = new JLabel("Consommation: 0L");

        panel.add(avgTimeLabel);
        panel.add(congestionLabel);
        panel.add(fuelLabel);

        panel.setBorder(BorderFactory.createTitledBorder("Statistiques en temps réel"));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrafficSimulatorGUI::new);
    }
}
