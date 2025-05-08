package Vue;

import Controle.*;
import Modele.*;
import Modele.Enregistrer.*;
import Modele.Signalling.TrafficLight;
import Modele.Vehicle.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.swing.*;

public class TrafficSimulatorGUI extends JFrame {
    private static final int ROWS = 50;
    private static final int COLS = 50;
    private static final double MAX_ZOOM = 2.5;
    private Controle ctrl;
    private JPanel mapPanel;
    private final JPanel controlPanel;
    private final JScrollPane scrollPane;
    private int cellSize = 100;
    private double zoomFactor = 1.0;
    private CityMap cityMap;
    private Point lastDragPoint;
    private boolean needUpdate;
    private final Timer uiTimer;
    private final Statistique stats;
    private Point highlightedCell = null;
    private final Stack2 selectedCells = new Stack2();
    private boolean imagesLoaded = false;
    private JLabel statusLabel;
    private LinkedList<Position> highlightedPath = null;

    public TrafficSimulatorGUI(CityMap cityMap, Statistique stats, Controle ctrl) {
        this.cityMap = cityMap;
        this.stats = stats;
        this.ctrl = ctrl;
        this.uiTimer = new Timer(500, createTimerListener());
        uiTimer.start();

        setTitle("Simulateur de gestion de trafic urbain");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
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

        controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        add(createChangePanel(), BorderLayout.WEST);
        addListener();

        new Timer(16, e -> {
            for (int i = 0; i < cityMap.getCols(); i++) {
                for (int j = 0; j < cityMap.getRows(); j++) {
                    Case c = cityMap.getGrid()[i][j];
                    Vehicle v = c.getVehicle();
                    if (v != null && v.isMovingTransition()) {
                        v.updateAnimationProgress(0.05f );
                    }
                }
            }
            repaint();
        }).start();

        setVisible(true);
        mapPanel.setFocusable(true);
        mapPanel.requestFocus();

        mapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!uiTimer.isRunning()) {
                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;
                    highlightedCell = new Point(x, y);
                    mapPanel.repaint();
                } else {
                    highlightedCell = null;
                    ctrl.getCaseUpdate().clear();
                }
            }
        });

        mapPanel.setFocusable(true);
        maxDezoom();
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

    private BufferedImage getTrafficCol(TrafficLight c) {
        return switch (c.getCol()) {
            case RED -> Image.imgTrafficRed;
            case ORANGE -> Image.imgTrafficOrange;
            case GREEN -> Image.imgTrafficGreen;
            default -> null;
        };
    }

    public void drawCase(Graphics g, int i, int j) {
        int x = j * cellSize;
        int y = i * cellSize;
        Case currentCase = cityMap.getGrid()[i][j];
    
        if (currentCase.getRoad() != null) {
            if (cityMap.getMode()) drawFreq(g, currentCase, x, y);
            else drawRoad(g, currentCase, x, y, i, j);
        } else if (currentCase.getBuilding() != null) {
            g.drawImage(Image.imgBuilding, x, y, cellSize, cellSize, null);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, cellSize, cellSize);
        }
    
        drawSelectionHighlights(g, x, y, i, j);
        drawHighlightMode(g, x, y, i, j);

        if (!uiTimer.isRunning() && highlightedPath != null && highlightedPath.contains(new Position(j, i))) {
            g.setColor(new Color(0, 0, 255, 100));
            g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
        }
    }

    private void drawFreq(Graphics g, Case currentCase, int x, int y) {
        double ratio = ((double) currentCase.getNbPassage()) / ((double) stats.getMaxPassage());
        int grad = (int) Math.floor(255 * (1 - ratio));
        g.setColor(new Color(255, grad, grad));
        g.fillRect(x, y, cellSize, cellSize);
    }
    
    private void drawRoad(Graphics g, Case currentCase, int x, int y, int i, int j) {
        Graphics2D g2d = (Graphics2D) g;
        Direction[] direction = currentCase.getRoad().getDirection();
        double angle = getRotationAngle(direction[0]);
    
        // Sauvegarder la transformation actuelle
        AffineTransform oldTransform = g2d.getTransform();
        
        // Dessiner la route droite
        g2d.translate(x + cellSize / 2, y + cellSize / 2);
        g2d.rotate(angle);
        g2d.drawImage(Image.imgRoad, -cellSize / 2, -cellSize / 2, cellSize, cellSize, null);
        // Restaurer la transformation
        g2d.setTransform(oldTransform);
    
        Direction[] curveDirections = getCurveDirections(currentCase, i, j);
        if (curveDirections != null) {
            Direction entry = curveDirections[0];
            Direction exit = curveDirections[1];
            double rotationAngle = getRotationAngle(entry, exit);
            
            // Appliquer la transformation pour le virage
            g2d.translate(x + cellSize/2, y + cellSize/2);
            g2d.rotate(rotationAngle);
            g2d.translate(-cellSize/2, -cellSize/2);
            
            g2d.drawImage(Image.imgVirage, 0, 0, cellSize, cellSize, null);
            // Restaurer la transformation
            g2d.setTransform(oldTransform);
        }
    
        if (currentCase.getRoad().isIntersection()) {
            g2d.drawImage(Image.intersection, x, y, cellSize, cellSize, null);
        }
    
        drawSignalling(g2d, currentCase, x, y, direction[0], angle);
    }

    private double getRotationAngle(Direction entry, Direction exit) {
        if ((entry == Direction.UP && exit == Direction.LEFT) || (entry == Direction.RIGHT && exit == Direction.DOWN)) return 0;
        else if ((entry == Direction.DOWN && exit == Direction.LEFT) || (entry == Direction.RIGHT && exit == Direction.UP)) return Math.PI/2;
        else if ((entry == Direction.DOWN && exit == Direction.RIGHT) || (entry == Direction.LEFT && exit == Direction.UP)) return Math.PI;
        else return 3*Math.PI/2;
    }
    
    private void drawVehicle(Graphics2D g2d, Vehicle vehicle) {
        float progress = vehicle.getAnimationProgress();
        Position oldPos = vehicle.getOldPosition();
        Position newPos = vehicle.getPosition();
        int oldX = oldPos.getX() * cellSize;
        int oldY = oldPos.getY() * cellSize;
        int newX = newPos.getX() * cellSize;
        int newY = newPos.getY() * cellSize;
        int drawX = (int) ((1 - progress) * oldX + progress * newX);
        int drawY = (int) ((1 - progress) * oldY + progress * newY);
        BufferedImage img = null;
        
        if (vehicle instanceof Car) img = Image.getImgCar(vehicle.getDirection());
        else if (vehicle instanceof Bus) img = Image.getImgBus(vehicle.getDirection());
        else if (vehicle instanceof Truck) img = Image.getImgCamion(vehicle.getDirection());
    
        if (img != null) g2d.drawImage(img, drawX, drawY, cellSize, cellSize, null);
    }
    
    private void drawSignalling(Graphics2D g2d, Case currentCase, int x, int y, Direction direction, double angle) {
        if (currentCase.getSignalling() == null) return;
        BufferedImage img = switch (currentCase.getSignalling().getType()) {
            case "TrafficLights" -> getTrafficCol((TrafficLight) currentCase.getSignalling());
            case "Stop" -> Image.imgStop;
            case "NoEntry" -> Image.imgNoEntry;
            default -> null;
        };
        if (img == null) return;
        
        int size = cellSize / 2;
        int signalX = x, signalY = y;
        switch (direction.toString()) {
            case "UP" -> { signalX = x + cellSize - size; signalY = y; }
            case "DOWN" -> { signalX = x; signalY = y + cellSize - size; }
            case "LEFT" -> { signalX = x; signalY = y; }
            case "RIGHT" -> { signalX = x + cellSize - size; signalY = y + cellSize - size; }
        }
    
        g2d.translate(signalX + size / 2, signalY + size / 2);
        g2d.rotate(angle);
        g2d.drawImage(img, -size / 2, -size / 2, size, size, null);
        g2d.rotate(-angle);
        g2d.translate(-signalX - size / 2, -signalY - size / 2);
    }
    
    private void drawSelectionHighlights(Graphics g, int x, int y, int i, int j) {
        if (selectedCells.isEmpty()) return;
        Position posA = selectedCells.getA();
        Position posB = selectedCells.getB();
        if (posA != null && posA.getX() == j && posA.getY() == i) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect(x, y, cellSize, cellSize);
        }
        if (posB != null && posB.getX() == j && posB.getY() == i) {
            g.setColor(new Color(0, 0, 255, 100));
            g.fillRect(x, y, cellSize, cellSize);
        }
    }
    
    private void drawHighlightMode(Graphics g, int x, int y, int i, int j) {
        if (!uiTimer.isRunning() && highlightedCell != null && highlightedCell.x == j && highlightedCell.y == i) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(x, y, cellSize, cellSize);
        }
    }
    
    private double getRotationAngle(Direction dir) {
        return switch (dir.toString()) {
            case "UP" -> 0;
            case "DOWN" -> Math.PI;
            case "RIGHT" -> Math.PI / 2;
            case "LEFT" -> -Math.PI / 2;
            default -> 0;
        };
    }
    
    private Direction[] getCurveDirections(Case currentCase, int i, int j) {
        if (currentCase.getRoad() == null || currentCase.getRoad().getDirection().length != 1) return null;
        Direction currentDirection = currentCase.getRoad().getDirection()[0];
        Direction incomingDirection = null;
        boolean isTurn = false;
        int[][] directionsToCheck = {
            {0, -1, Direction.RIGHT.ordinal()}, {0, 1, Direction.LEFT.ordinal()},
            {-1, 0, Direction.DOWN.ordinal()}, {1, 0, Direction.UP.ordinal()}
        };
    
        for (int[] dir : directionsToCheck) {
            int newX = i + dir[0];
            int newY = j + dir[1];
            if (newX >= 0 && newX < cityMap.getGrid().length && newY >= 0 && newY < cityMap.getGrid()[0].length && 
                cityMap.getGrid()[newX][newY].getRoad() != null) {
                Direction neighborDirection = cityMap.getGrid()[newX][newY].getRoad().getDirection()[0];
                if (neighborDirection.ordinal() == dir[2]) {
                    if (incomingDirection != null) return null;
                    incomingDirection = neighborDirection;
                    isTurn = (neighborDirection != currentDirection);
                }
            }
        }
        return (incomingDirection != null && isTurn) ? new Direction[]{incomingDirection, currentDirection} : null;
    }

    public void drawMap(Graphics g) {
        if (!imagesLoaded) { Image.loadImg(); imagesLoaded = true; }
        Rectangle visibleRect = mapPanel.getVisibleRect();
        int startCol = Math.max(0, visibleRect.x / cellSize - 1);
        int startRow = Math.max(0, visibleRect.y / cellSize - 1);
        int endCol = Math.min(COLS, (visibleRect.x + visibleRect.width) / cellSize + 1);
        int endRow = Math.min(ROWS, (visibleRect.y + visibleRect.height) / cellSize + 1);
    
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol; j++) {
                drawCase(g, i, j);
            }
        }
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < cityMap.getRows(); i++) {
            for (int j = 0; j < cityMap.getCols(); j++) {
                Vehicle v = cityMap.getGrid()[i][j].getVehicle();
                if (v != null) drawVehicle(g2d, v);
            }
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem saveItem = new JMenuItem("Sauvegarder");
        JMenuItem loadItem = new JMenuItem("Charger");
        JMenuItem exitItem = new JMenuItem("Quitter");
        saveItem.addActionListener(e -> saveMap());
        exitItem.addActionListener(e -> System.exit(0));
        loadItem.addActionListener(e -> loadMap());
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
        JLabel avgTimeLabel = new JLabel("Temps de trajet moyen: 0u");
        JLabel congestionLabel = new JLabel("Embouteillages: 0%");
        JLabel nbVoitureLabel = new JLabel("Nombre de Voiture: 0");
        JLabel tempsArret = new JLabel("Temps à l'arret moyen: 0");
        panel.add(nbVoitureLabel);
        panel.add(avgTimeLabel);
        panel.add(congestionLabel);
        panel.add(tempsArret);
        panel.setBorder(BorderFactory.createTitledBorder("Statistiques en temps réel"));
        return panel;
    }

    private JPanel createDirectionSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Choisir la direction"));
        String[][] directions = {{"Haut", "UP"}, {"Bas", "DOWN"}, {"Gauche", "LEFT"}, {"Droite", "RIGHT"}};
    
        for (String[] dir : directions) {
            JButton button = new JButton(dir[0]);
            button.addActionListener(e -> {
                ctrl.setChange("route:" + dir[1]);
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window != null) window.dispose();
                selectedCells.clear();
                mapPanel.repaint();
            });
            panel.add(button);
        }
        return panel;
    }
    
    private JPanel createChangePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Action Utilisateur"));
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateStatusLabel();
        panel.add(statusLabel);

        Box box = Box.createVerticalBox();
        JButton maisonButton = makeButton("Mettre une maison", "maison");
        JButton routeButton = new JButton("Mettre une route/changer rotation");
        JButton feuButton = makeButton("Mettre un Feu/changer de groupe", "feu");
        JButton voitureButton = makeButton("Mettre une voiture", "voiture");
        JButton tabFreq = new JButton("Montrer la fréquence de passage des cases");
        tabFreq.addActionListener(e -> { cityMap.setMode(!cityMap.getMode()); mapPanel.requestFocus(); });

        box.add(maisonButton);
        box.add(routeButton);
        box.add(feuButton);
        box.add(voitureButton);
        box.add(tabFreq);

        JLabel speedLabel = new JLabel("Speed", JLabel.CENTER);
        box.add(speedLabel);
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, ctrl.getSpeed());
        speedSlider.setMajorTickSpacing(2);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setInverted(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(20, new JLabel("-"));
        labelTable.put(2, new JLabel("+"));
        speedSlider.setLabelTable(labelTable);
        speedSlider.addChangeListener(e -> { ctrl.setSpeed(speedSlider.getValue()); ctrl.modifySpeed(); mapPanel.requestFocus(); });
        box.add(speedSlider);

        routeButton.addActionListener(e -> {
            if (!uiTimer.isRunning() && !ctrl.getCaseUpdate().isEmpty()) {
                JDialog directionDialog = new JDialog(TrafficSimulatorGUI.this, "Sélection de direction", true);
                directionDialog.add(createDirectionSelectionPanel());
                directionDialog.pack();
                directionDialog.setLocationRelativeTo(TrafficSimulatorGUI.this);
                directionDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(panel, "Mettez en pause et sélectionnez au moins une case.", "Action impossible", JOptionPane.WARNING_MESSAGE);
            }
            mapPanel.requestFocus();
        });

        JLabel instruct = new JLabel(
            "<html>Pour ajouter/retirer des objets:<br>" +"- Mettre en pause la simulation en cliquant sur P<br>" +"- Appuyer sur les cases à modifier<br>" +"- Appuyer sur le changement à effectuer</html>",
            SwingConstants.CENTER
        );
        instruct.setFont(new Font("Arial", Font.PLAIN, 14));
        instruct.setForeground(Color.BLACK);
        instruct.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        instruct.setPreferredSize(new Dimension(300, 100));

        Box box2 = Box.createVerticalBox();
        box2.add(instruct);
        panel.add(box);
        panel.add(box2);
        return panel;
    }

    private void updateStatusLabel() {
        if (statusLabel != null) {
            if (!uiTimer.isRunning()) { statusLabel.setText("Pause"); statusLabel.setForeground(Color.RED); }
            else { statusLabel.setText("Play"); statusLabel.setForeground(new Color(0, 150, 0)); }
        }
    }

    private void zoom(boolean zoomIn, Point cursorPosition) {
        if (zoomIn) { if (zoomFactor < MAX_ZOOM) zoomFactor += 0.1; } 
        else { if (zoomFactor > 0.5) zoomFactor -= 0.1; }
    
        cellSize = (int) (40 * zoomFactor);
        mapPanel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
        mapPanel.revalidate();
        mapPanel.repaint();
    
        if (cursorPosition != null) {
            JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int newCenterX = (int) (cursorPosition.x * zoomFactor);
            int newCenterY = (int) (cursorPosition.y * zoomFactor);
            horizontal.setValue(Math.max(newCenterX - scrollPane.getWidth() / 2, 0));
            vertical.setValue(Math.max(newCenterY - scrollPane.getHeight() / 2, 0));
        }
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    private MouseWheelListener createMouseWheelZoomListener() {
        return e -> {
            Point cursorPosition = e.getPoint();
            if (e.getPreciseWheelRotation() < 0) zoom(true, cursorPosition);
            else zoom(false, cursorPosition);
        };
    }

    private MouseListener createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / cellSize, col = e.getX() / cellSize;
                Position pos = new Position(col, row);
    
                if (!uiTimer.isRunning()) {
                    ctrl.getCaseUpdate().add(pos);
                    selectedCells.add(pos);
                }
    
                Vehicle v = cityMap.getVehicleAt(pos);
                highlightedPath = (v != null && v.path != null) ? new LinkedList<>(v.path) : null;
                mapPanel.repaint();
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                highlightedCell = null;
                mapPanel.repaint();
            }
        };
    }
    
    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    scrollPane.getHorizontalScrollBar().setValue(
                        scrollPane.getHorizontalScrollBar().getValue() + lastDragPoint.x - e.getX());
                    scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getValue() + lastDragPoint.y - e.getY());
                }
                lastDragPoint = e.getPoint();
            }
    
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                if (x >= 0 && x < mapPanel.getWidth() && y >= 0 && y < mapPanel.getHeight() && !uiTimer.isRunning()) {
                    highlightedCell = new Point(x / cellSize, y / cellSize);
                } else {
                    highlightedCell = null;
                    if (uiTimer.isRunning()) ctrl.getCaseUpdate().clear();
                }
                mapPanel.repaint();
            }
        };
    }
    

    private void saveMap() {
        String fileName = JOptionPane.showInputDialog(this, "Entrez le nom de la sauvegarde :", "Sauvegarder", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            boolean success = GameSaver.saveGame(fileName, cityMap);
            if (success) JOptionPane.showMessageDialog(this, "Sauvegarde réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Le nom de la sauvegarde ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMap() {
        ArrayList<String> saveNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Save/nom_save.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) saveNames.add(line.trim());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la lecture des sauvegardes.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (saveNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune sauvegarde disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> saveComboBox = new JComboBox<>(saveNames.toArray(String[]::new));
        saveComboBox.setSelectedIndex(0);
        int option = JOptionPane.showConfirmDialog(this, saveComboBox, "Choisissez une sauvegarde", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String selectedSave = (String) saveComboBox.getSelectedItem();
            CityMap loadedMap = GameLoader.loadGame(selectedSave, stats, ctrl);
            if (loadedMap != null) {
                this.cityMap = loadedMap;
                mapPanel.repaint();
                JOptionPane.showMessageDialog(this, "Chargement réussi !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la carte.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private KeyListener createKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'p') {
                    if (uiTimer.isRunning()) {uiTimer.stop(); highlightedPath = null;}
                    else { uiTimer.start(); selectedCells.clear(); mapPanel.repaint(); }
                    updateStatusLabel();
                }
            }
        };
    }

    private ActionListener createTimerListener() {
        return (ActionEvent e) -> {
            if (needUpdate) {
                if (controlPanel != null) {
                    UpdateStats.update(controlPanel.getComponents(), stats);
                    controlPanel.repaint();
                    stats.resetMoved();
                }
                mapPanel.repaint();
                needUpdate = false;
            }
        };
    }

    private void addListener() {
        mapPanel.addMouseWheelListener(createMouseWheelZoomListener());
        mapPanel.addMouseListener(createMouseListener());
        mapPanel.addMouseMotionListener(createMouseMotionListener());
        mapPanel.addKeyListener(createKeyListener());
    }

    private JButton makeButton(String s1, String s2) {
        JButton button = new JButton(s1);
        button.addActionListener((ActionEvent e) -> {
            if (!uiTimer.isRunning() && !ctrl.getCaseUpdate().isEmpty()) {
                ctrl.setChange(s2);
                selectedCells.clear();
                mapPanel.repaint();
            }
            mapPanel.requestFocus();
        });
        return button;
    }

    public boolean isRunning() { return (uiTimer != null) ? uiTimer.isRunning() : false; }
    public void setUpdate(boolean b) { needUpdate = b; }
    private void maxDezoom() { for (int i = 0; i < 6; i++) zoom(false, null); }
}