package Vue;

import Modele.CityMap;
import Modele.Direction;
import Modele.Position;
import Modele.AffichageTemp;
import Modele.Case;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class TrafficSimulatorGUI extends JFrame {

    private static final int ROWS = 20;
    private static final int COLS = 20;
    private JPanel mapPanel;
    private JScrollPane scrollPane;
    private int cellSize = 40;
    private double zoomFactor = 1.0;

    private CityMap cityMap; // Référence à la carte de la ville
    private Point lastDragPoint; // Ajoutez cette ligne pour déclarer lastDragPoint

    public TrafficSimulatorGUI(CityMap cityMap) {
        this.cityMap = cityMap;

        setTitle("Simulateur de gestion de trafic urbain");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        setLocationRelativeTo(null);

        JPanel containerPanel = new JPanel(new GridBagLayout());
        mapPanel = createMapPanel();
        containerPanel.add(mapPanel, new GridBagConstraints());
        scrollPane = new JScrollPane(containerPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        mapPanel.addMouseWheelListener(createMouseWheelZoomListener());
        mapPanel.addMouseListener(createMouseDragListener());
        mapPanel.addMouseMotionListener(createMouseMotionListener());

        setVisible(true);
    }

    private JPanel createMapPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        panel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
        return panel;
    }

    private void drawMap(Graphics g) {
        Case[][] grid = cityMap.getGrid();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = j * cellSize;
                int y = i * cellSize;

                Case currentCase = grid[i][j];

                // Dessiner la route si elle existe
                if (currentCase.getRoad() != null) {
                    g.setColor(Color.GRAY);
                    g.fillRect(x, y, cellSize, cellSize);
                }
                // Dessiner le bâtiment si il existe
                else if (currentCase.getBuilding() != null) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(x, y, cellSize, cellSize);
                }
                // Dessiner le véhicule si il existe
                else if (currentCase.getVehicle() != null) {
                    g.setColor(Color.RED);
                    g.fillRect(x, y, cellSize, cellSize);
                }
                // Dessiner une case vide
                else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x, y, cellSize, cellSize);
                }

                // Dessiner les bordures
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

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

        JMenu simulationMenu = new JMenu("Simulation");
        JMenuItem startItem = new JMenuItem("Démarrer");
        JMenuItem stopItem = new JMenuItem("Arrêter");

        simulationMenu.add(startItem);
        simulationMenu.add(stopItem);
        menuBar.add(simulationMenu);

        JMenu viewMenu = new JMenu("Affichage");
        JMenuItem zoomInItem = new JMenuItem("Zoom avant");
        JMenuItem zoomOutItem = new JMenuItem("Zoom arrière");

        zoomInItem.addActionListener(e -> zoom(true, null));
        zoomOutItem.addActionListener(e -> zoom(false, null));

        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        menuBar.add(viewMenu);

        return menuBar;
    }

    private JPanel createControlPanel() {
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

    private void zoom(boolean zoomIn, Point cursorPosition) {
        if (zoomIn) {
            zoomFactor += 0.1;
        } else if (zoomFactor > 0.2) {
            zoomFactor -= 0.1;
        }

        cellSize = (int) (40 * zoomFactor);
        mapPanel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
        mapPanel.revalidate();

        if (cursorPosition != null) {
            JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();

            int newCenterX = (int) (cursorPosition.x * zoomFactor);
            int newCenterY = (int) (cursorPosition.y * zoomFactor);

            horizontal.setValue(Math.max(newCenterX - scrollPane.getWidth() / 2, 0));
            vertical.setValue(Math.max(newCenterY - scrollPane.getHeight() / 2, 0));
        }

        mapPanel.repaint();
    }

    private MouseWheelListener createMouseWheelZoomListener() {
        return e -> {
            Point cursorPosition = e.getPoint();
            if (e.getPreciseWheelRotation() < 0) {
                zoom(true, cursorPosition);
            } else {
                zoom(false, cursorPosition);
            }
        };
    }

    private MouseListener createMouseDragListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        };
    }

    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();

                    int deltaX = lastDragPoint.x - e.getX();
                    int deltaY = lastDragPoint.y - e.getY();

                    horizontal.setValue(horizontal.getValue() + deltaX);
                    vertical.setValue(vertical.getValue() + deltaY);
                }
                lastDragPoint = e.getPoint();
            }
        };
    }

    public static void main(String[] args) {
        // Créer une carte de 100x100 cases
        CityMap cityMap = new CityMap(20, 20);
    
        // Ajouter des bâtiments partout sauf sur les routes
        cityMap.setBuilding();
    
        // Ajouter des routes horizontales
        for (int i = 0; i < 20; i++) {
            cityMap.setRoad(new Position(i, 10), Direction.RIGHT, "Route Horizontale 1");
            cityMap.setRoad(new Position(i, 11), Direction.RIGHT, "Route Horizontale 1");
            cityMap.setRoad(new Position(i, 50), Direction.RIGHT, "Route Horizontale 2");
        }
    
        // Ajouter des routes verticales
        for (int i = 0; i < 20; i++) {
            cityMap.setRoad(new Position(2, i), Direction.DOWN, "Route Verticale 1");
            cityMap.setRoad(new Position(8, i), Direction.DOWN, "Route Verticale 2");
        }
    

    
        // Afficher la carte dans la console (pour vérification)
        AffichageTemp.affiche(cityMap.getGrid());  
          
        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> new TrafficSimulatorGUI(cityMap));
    }
}