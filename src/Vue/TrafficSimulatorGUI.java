package Vue;

import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Position;
import Modele.Signalling.TrafficLight;
import Modele.Signalling.TrafficLightGroup;
import Modele.Simulation;
import Modele.Enregistrer.GameLoader;
import Modele.Enregistrer.GameSaver;
import Modele.Vehicle.Car;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

import Controle.Controle;



public class TrafficSimulatorGUI extends JFrame {

    private static final int ROWS = 50;
    private static final int COLS = 50;
    private JPanel mapPanel;
    private JScrollPane scrollPane;
    private int cellSize = 100;
    private double zoomFactor = 1.0;

    private CityMap cityMap; // Référence à la carte de la ville
    private Point lastDragPoint; // Ajoutez cette ligne pour déclarer lastDragPoint

    private static boolean needUpdate;
    private Timer uiTimer;

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

        add(createChangePanel(), BorderLayout.WEST);

        addListener();

        uiTimer = new Timer(500, createTimerListener());
        uiTimer.setInitialDelay(0);
        uiTimer.start();

        setVisible(true);
        
        mapPanel.setFocusable(true);
        mapPanel.requestFocus();
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

        JLabel avgTimeLabel = new JLabel("Temps moyen: 0s");
        JLabel congestionLabel = new JLabel("Embouteillages: 0%");
        JLabel fuelLabel = new JLabel("Consommation: 0L");

        panel.add(avgTimeLabel);
        panel.add(congestionLabel);
        panel.add(fuelLabel);

        panel.setBorder(BorderFactory.createTitledBorder("Statistiques en temps réel"));
        return panel;
    }

    private JPanel createChangePanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));

        JButton maisonButton = makeButton("Mettre une maison", "maison");
        JButton routeButton = makeButton("Mettre une route/changer rotation", "route");
        JButton feuButton = makeButton("Mettre un Feu/changer de groupe", "feu");
        JButton voitureButton = makeButton("Mettre une voiture", "voiture");

        Box box = Box.createVerticalBox();

        box.add(maisonButton);
        box.add(routeButton);
        box.add(feuButton);
        box.add(voitureButton);
        

        panel.add(box);
        panel.setBorder(BorderFactory.createTitledBorder("Action Utilisateur"));

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

    private MouseListener createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e){
                int x = e.getY()/cellSize;
                int y = e.getX()/cellSize;
                if (!Controle.timerIsRunning){
                    Controle.caseUpdate.add(new Position(y, x));
                    System.out.println(new Position(y, x));
                }
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



    private void saveMap() {
        // Demander à l'utilisateur un nom de fichier pour la sauvegarde
        String fileName = JOptionPane.showInputDialog(this, "Entrez le nom de la sauvegarde :", "Sauvegarder", JOptionPane.PLAIN_MESSAGE);

        if (fileName != null && !fileName.trim().isEmpty()) {
            // Appeler la classe GameSaver pour sauvegarder la carte
            boolean success = GameSaver.saveGame(fileName, cityMap);

            if (success) {
                JOptionPane.showMessageDialog(this, "Sauvegarde réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Le nom de la sauvegarde ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Charge une carte à partir d'un fichier de sauvegarde.
     */
    private void loadMap() {
        // Lire le fichier nom_save.txt pour obtenir la liste des sauvegardes
        ArrayList<String> saveNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Save/nom_save.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                saveNames.add(line.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la lecture des sauvegardes.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier s'il y a des sauvegardes disponibles
        if (saveNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune sauvegarde disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un JComboBox avec les noms des sauvegardes
        JComboBox<String> saveComboBox = new JComboBox<>(saveNames.toArray(new String[0]));
        saveComboBox.setSelectedIndex(0); // Sélectionner la première sauvegarde par défaut

        // Afficher la boîte de dialogue avec le JComboBox
        int option = JOptionPane.showConfirmDialog(this, saveComboBox, "Choisissez une sauvegarde", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            // Récupérer le nom de la sauvegarde sélectionnée
            String selectedSave = (String) saveComboBox.getSelectedItem();

            // Appeler la classe GameLoader pour charger la carte
            CityMap loadedMap = GameLoader.loadGame(selectedSave);

            if (loadedMap != null) {
                // Mettre à jour la carte dans l'interface graphique
                this.cityMap = loadedMap;
                mapPanel.repaint(); // Rafraîchir l'affichage
                JOptionPane.showMessageDialog(this, "Chargement réussi !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la carte.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private KeyListener createKeyListener(){
        return new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                switch(e.getKeyChar()){
                    case 'p' : {
                        if (uiTimer.isRunning()){
                            uiTimer.stop();
                            Controle.timerIsRunning = false;
                        }else{
                            uiTimer.start();
                            Controle.timerIsRunning = true;
                        }
                    }
                }
            }
        };
    }

    private ActionListener createTimerListener(){
        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (needUpdate){
                    mapPanel.repaint();
                    needUpdate = false;
                }
            }
        };
    }

    private void addListener(){
        mapPanel.addMouseWheelListener(createMouseWheelZoomListener());
        mapPanel.addMouseListener(createMouseListener());
        mapPanel.addMouseMotionListener(createMouseMotionListener());
        mapPanel.addKeyListener(createKeyListener());
    }

    private JButton makeButton(String s1, String s2){
        JButton button = new JButton(s1);
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (!Controle.timerIsRunning && Controle.caseUpdate != null){
                    System.out.println(s2);
                    Controle.setChange(s2);
                }
                mapPanel.requestFocus();
            }
        });

        return button;
    }
    
    public static void setUpdate(boolean b){
        needUpdate = b;
    }

    public void pause(){
        uiTimer.stop();
    }

    public void start(){
        uiTimer.start();
    }


}