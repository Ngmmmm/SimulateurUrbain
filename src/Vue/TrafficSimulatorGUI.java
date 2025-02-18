package Vue;

import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Pathfinding;
import Modele.Position;
import Modele.Signalling.LightColor;
import Modele.Signalling.TrafficLight;
import Modele.Signalling.TrafficLightGroup;
import Modele.Simulation;
import Modele.Vehicle.Car;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

    private BufferedImage getTrafficCol(TrafficLight c){
        return switch (c.getCol()){
            case RED -> Image.imgTrafficRed;
            case ORANGE -> Image.imgTrafficOrange;
            case GREEN -> Image.imgTrafficGreen;
            default -> null;
        };
    } 
    //dessine la case selon sa place et ses propriétés
    public void drawCase(Graphics g, int i, int j){
        int x = j * cellSize;
        int y = i * cellSize;
        Case currentCase = cityMap.getGrid()[i][j];

        // Dessiner la route si elle existe
        if (currentCase.getRoad() != null) {
            g.drawImage(Image.imgRoad, x, y, null);
                    
            // Dessiner le véhicule si il existe
            if (currentCase.getVehicle() != null) {
                g.drawImage(Image.imgCar , x, y, null);
            }

            // Dessiner un signalement si il existe
            if (currentCase.getSignalling() != null){
                BufferedImage img = switch (currentCase.getSignalling().getType()){
                    case "TrafficLights" -> getTrafficCol((TrafficLight) currentCase.getSignalling());
                    case "Stop" -> Image.imgStop;
                    case "NoEntry" -> Image.imgNoEntry;
                    default -> null;
                };
                g.drawImage(img, x, y, null);
            }
        }
        // Dessiner le bâtiment si il existe
        else if (currentCase.getBuilding() != null) {
            g.drawImage(Image.imgBuilding, x, y, null);
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

    private static final double MAX_ZOOM = 2.5;  // Le facteur de zoom maximal autorisé

    private void zoom(boolean zoomIn, Point cursorPosition) {
        if (zoomIn) {
            
            if (zoomFactor < MAX_ZOOM) {
                zoomFactor += 0.1;
            }
        } else {
            
            if (zoomFactor > 0.2) {
                zoomFactor -= 0.1;
            }
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


}