package Vue;

import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Position;
import Modele.TuplePosCase;
import Modele.AffichageTemp;
import Modele.Case;


import javax.swing.*;

import Controle.UpdateInter;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;


public class TrafficSimulatorGUI extends JFrame implements UpdateInter {

    private static final int ROWS = 50;
    private static final int COLS = 50;
    private JPanel mapPanel;
    private JScrollPane scrollPane;
    private int cellSize = 100;
    private double zoomFactor = 1.0;

    private CityMap cityMap;
    private Point lastDragPoint;
    private Case[][] grid; // Stockage de la grille pour mises à jour dynamiques
    private ArrayList<TuplePosCase> toUpdate = new ArrayList<TuplePosCase>();

    public TrafficSimulatorGUI(CityMap cityMap, Case[][] grid) {
        this.cityMap = cityMap;
        this.grid = grid; // Initialiser la grille

        setTitle("Simulateur de gestion de trafic urbain");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        setLocationRelativeTo(null);

        JPanel containerPanel = new JPanel(new GridBagLayout());
        mapPanel = createMapPanel();
        mapPanel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
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
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
    }

    private void drawCase(Graphics g, int i, int j, Case currentCase) {
        int x = j * cellSize;
        int y = i * cellSize;

        if (currentCase.getRoad() != null) {
            g.drawImage(Image.imgRoad, x, y, null);

            if (currentCase.getVehicle() != null) {
                g.drawImage(Image.imgCar, x, y, null);
            }

            if (currentCase.getSignalling() != null) {
                BufferedImage img = switch (currentCase.getSignalling().getType()) {
                    case "TrafficLights" -> Image.imgTrafficLight;
                    case "Stop" -> Image.imgStop;
                    case "NoEntry" -> Image.imgNoEntry;
                    default -> null;
                };
                g.drawImage(img, x, y, null);
            }
        } else if (currentCase.getBuilding() != null) {
            g.drawImage(Image.imgBuilding, x, y, null);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, cellSize, cellSize);
        }

        g.setColor(Color.BLACK);
        g.drawRect(x, y, cellSize, cellSize);
    }

    private void drawMap(Graphics g) {
        if (grid == null) return; // Ne rien dessiner si grid est null
        Image.loadImg();
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j] != null) { // Vérifie que la case existe
                    drawCase(g, i, j, grid[i][j]);
                }
            }
        }
    }
    

    @Override
    public void update() {
        // va update toutes les cases qui ont été mises dans la liste des case a update 
        for(TuplePosCase tileAt : toUpdate){
            grid[tileAt.getX()][tileAt.getY()].update(tileAt.getCase());
            this.drawCase(getGraphics(), tileAt.getX(), tileAt.getY(), tileAt.getCase());
        }
        toUpdate.clear();
        mapPanel.repaint();
    }

    public void updateVue(){
        this.drawMap(getGraphics());
        mapPanel.repaint();
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



    public static void main(String[] args) {
        Simulation s = new Simulation();
        CityMap cityMap = s.getCityMap();

        Position centerPosition = new Position(25, 25);

        // Ajouter une voiture si la position est une route
        Car car = new Car("rouge", (double)10, centerPosition , Direction.UP, 1);
        cityMap.getGrid()[centerPosition.getY()][centerPosition.getX()].setVehicle(car);

        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> new TrafficSimulatorGUI(cityMap));
    }

}