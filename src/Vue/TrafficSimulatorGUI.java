package Vue;

import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Signalling.TrafficLight;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;


public class TrafficSimulatorGUI extends JFrame {

    private static final int ROWS = 50;
    private static final int COLS = 50;
    private JPanel mapPanel;
    private JScrollPane scrollPane;
    private int cellSize = 100;
    private double zoomFactor = 1.0;

    private CityMap cityMap; // Référence à la carte de la ville
    private Point lastDragPoint; // Ajoutez cette ligne pour déclarer lastDragPoint

    public TrafficSimulatorGUI(CityMap cityMap) {
        this.cityMap = cityMap;

        setTitle("Simulateur de gestion de trafic urbain");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
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

    private BufferedImage getTrafficCol(TrafficLight c){
        return switch (c.getCol()){
            case RED -> Image.imgTrafficRed;
            case ORANGE -> Image.imgTrafficOrange;
            case GREEN -> Image.imgTrafficGreen;
            default -> null;
        };
    } 
    //dessine la case selon sa place et ses propriétés
    public void drawCase(Graphics g, int i, int j) {
        int x = j * cellSize;
        int y = i * cellSize;
        Case currentCase = cityMap.getGrid()[i][j];
    
        if (currentCase.getRoad() != null) {
            Graphics2D g2d = (Graphics2D) g;
    
            // Déterminer l'angle de rotation en fonction de la direction
            double angle = 0;
            Direction[] direction = currentCase.getRoad().getDirection();
            switch (direction[0].toString()) {
                case "UP" -> angle = 0;
                case "DOWN" -> angle = Math.PI;
                case "RIGHT" -> angle = Math.PI / 2;
                case "LEFT" -> angle = -Math.PI / 2;
            }
    
            // Appliquer la rotation pour la route
            g2d.translate(x + cellSize / 2, y + cellSize / 2);
            g2d.rotate(angle);
            g2d.drawImage(Image.imgRoad, -cellSize / 2, -cellSize / 2, cellSize, cellSize, null);
            g2d.rotate(-angle);
            g2d.translate(-x - cellSize / 2, -y - cellSize / 2);
            
            // if (isCurve(currentCase, i, j)) {
            //     g2d.drawImage(Image.imgVirage, x, y, cellSize, cellSize, null);
            // }
            
            // Dessiner le véhicule s'il existe
            if (currentCase.getVehicle() != null) {
                g2d.drawImage(Image.getImgCar(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
            }
    
            // Dessiner un panneau s'il existe
            if (currentCase.getSignalling() != null) {
                BufferedImage img = switch (currentCase.getSignalling().getType()) {
                    case "TrafficLights" -> getTrafficCol((TrafficLight) currentCase.getSignalling());
                    case "Stop" -> Image.imgStop;
                    case "NoEntry" -> Image.imgNoEntry;
                    default -> null;
                };
                int size = cellSize / 2;
    
                // Position du panneau
                int signalX = x;
                int signalY = y;
    
                switch (direction[0].toString()) {
                    case "UP" -> {
                        signalX = x + cellSize - size; // En haut à droite
                        signalY = y;
                    }
                    case "DOWN" -> {
                        signalX = x; // En bas à gauche
                        signalY = y + cellSize - size;
                    }
                    case "LEFT" -> {
                        signalX = x; // À gauche en haut
                        signalY = y;
                    }
                    case "RIGHT" -> {
                        signalX = x + cellSize - size; // À droite en haut
                        signalY = y + cellSize - size;
                    }
                }
    
                // Appliquer la même rotation que la route au panneau
                g2d.translate(signalX + size / 2, signalY + size / 2);
                g2d.rotate(angle);
                g2d.drawImage(img, -size / 2, -size / 2, size, size, null);
                g2d.rotate(-angle);
                g2d.translate(-signalX - size / 2, -signalY - size / 2);

                // Dessiner le véhicule s'il existe
            if (currentCase.getVehicle() != null) {
                g2d.drawImage(Image.getImgCar(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
            }
            }
        }
        // Dessiner le bâtiment s'il existe
        else if (currentCase.getBuilding() != null) {
            g.drawImage(Image.imgBuilding, x, y, cellSize, cellSize, null);
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
    
    // private boolean isCurve(Case currentCase, int i, int j) {
    //     if (currentCase.getRoad() == null) {
    //         return false;
    //     }
    
    //     Direction[] currentDirections = currentCase.getRoad().getDirection();
    
    //     // Vérifier la case devant
    //     int nextI = i, nextJ = j;
    //     switch (currentDirections[0].toString()) {
    //         case "UP" -> nextI--;
    //         case "DOWN" -> nextI++;
    //         case "LEFT" -> nextJ--;
    //         case "RIGHT" -> nextJ++;
    //     }
    
    //     // Vérifier si la case devant existe dans la grille
    //     if (nextI < 0 || nextI >= cityMap.getGrid().length || nextJ < 0 || nextJ >= cityMap.getGrid()[0].length) {
    //         return false;
    //     }
    
    //     Case nextCase = cityMap.getGrid()[nextI][nextJ];
    
    //     // Vérifier si la case devant est une route et a une direction différente
    //     return nextCase.getRoad() != null && nextCase.getRoad().getDirection()[0] != currentDirections[0];
    // }
    

    public void drawMap(Graphics g) {
        Image.loadImg();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                drawCase(g, i, j);
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

    private static final double MAX_ZOOM = 2.5;

    private void zoom(boolean zoomIn, Point cursorPosition) {
        if (zoomIn) {
            if (zoomFactor < MAX_ZOOM) {
                zoomFactor += 0.1;
            }
        } else {
            if (zoomFactor > 0.5) {
                zoomFactor -= 0.1;
            }
        }
    
        // Recalculer la taille des cellules en fonction du zoom
        cellSize = (int) (40 * zoomFactor);
    
        // Recalculer la taille du mapPanel
        mapPanel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
        mapPanel.revalidate(); // Forcer la mise à jour du layout
        mapPanel.repaint();    // Redessiner la carte
    
        // Ajuster la position de la vue dans le JScrollPane
        if (cursorPosition != null) {
            JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
    
            // Calculer la nouvelle position de la vue
            int newCenterX = (int) (cursorPosition.x * zoomFactor);
            int newCenterY = (int) (cursorPosition.y * zoomFactor);
    
            // Ajuster les barres de défilement pour centrer la vue sur le point de zoom
            horizontal.setValue(Math.max(newCenterX - scrollPane.getWidth() / 2, 0));
            vertical.setValue(Math.max(newCenterY - scrollPane.getHeight() / 2, 0));
        }

        scrollPane.revalidate();
        scrollPane.repaint();
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


}