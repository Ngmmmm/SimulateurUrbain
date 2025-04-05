package Vue;

import Modele.Case;
import Modele.CityMap;
import Modele.Direction;
import Modele.Position;
import Modele.Stack2;
import Modele.Statistique;
import Modele.Enregistrer.GameLoader;
import Modele.Enregistrer.GameSaver;
import Modele.Signalling.TrafficLight;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;

import Controle.Controle;
import Controle.UpdateStats;
import Modele.Signalling.TrafficLight;
import Modele.Vehicle.Bus;
import Modele.Vehicle.Car;
import Modele.Vehicle.Truck;



public class TrafficSimulatorGUI extends JFrame {

    private static final int ROWS = 50;
    private static final int COLS = 50;
    private JPanel mapPanel;
    private JPanel controlPanel;
    private JScrollPane scrollPane;
    private int cellSize = 100;
    private double zoomFactor = 1.0;

    private CityMap cityMap; // Référence à la carte de la ville
    private Point lastDragPoint; // Ajoutez cette ligne pour déclarer lastDragPoint

    private static boolean needUpdate;
    private static boolean pause = false;
    private Timer uiTimer;
    private Statistique stats;

    private Point highlightedCell = null; // Case en surbrillance (jaune)
    private Stack2 selectedCells = new Stack2();

    private boolean imagesLoaded = false;

    public TrafficSimulatorGUI(CityMap cityMap, Statistique stats) {
        this.cityMap = cityMap;
        this.stats = stats;

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

        uiTimer = new Timer(500, createTimerListener());
        uiTimer.setInitialDelay(0);
        uiTimer.start();

        setVisible(true);
        
        mapPanel.setFocusable(true);
        mapPanel.requestFocus();

        mapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (TrafficSimulatorGUI.getPause()) {
                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;
                    highlightedCell = new Point(x, y);
                    mapPanel.repaint();
                } else {
                    highlightedCell = null;
                    Controle.caseUpdate.clear();
                }
            }
        });

        mapPanel.setFocusable(true); // Permettre au panel de recevoir les événements clavier
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
            
            if (isCurve(currentCase, i, j)) {
                g2d.drawImage(Image.imgVirage, x, y, cellSize, cellSize, null);
            }

            if (currentCase.getRoad().isIntersection()) {
                g2d.drawImage(Image.intersection, x, y, cellSize, cellSize, null);
            }
            if (isCurve(currentCase, i, j)) {
                g2d.drawImage(Image.imgVirage, x, y, cellSize, cellSize, null);
            }

            if (currentCase.getRoad().isIntersection()) {
                g2d.drawImage(Image.intersection, x, y, cellSize, cellSize, null);
            }
            
            // Dessiner le véhicule s'il existe
            if (currentCase.getVehicle() != null) {
                if(currentCase.getVehicle() instanceof Car){
                    g2d.drawImage(Image.getImgCar(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
                }
                else if (currentCase.getVehicle() instanceof Bus){
                    g2d.drawImage(Image.getImgBus(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
                }
                else if (currentCase.getVehicle() instanceof Truck){
                    g2d.drawImage(Image.getImgCamion(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
                }
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
                    if(currentCase.getVehicle() instanceof Car){
                        g2d.drawImage(Image.getImgCar(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
                    }
                    else if (currentCase.getVehicle() instanceof Bus){
                        g2d.drawImage(Image.getImgBus(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
                    }
                    else if (currentCase.getVehicle() instanceof Truck){
                        g2d.drawImage(Image.getImgCamion(currentCase.getVehicle().getDirection()), x, y, cellSize, cellSize, null);
                    }
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

        // Dessiner les surlignages
        if (!selectedCells.isEmpty()) {
            Position posA = selectedCells.getA();
            Position posB = selectedCells.getB();
            
            // Case rouge (la plus récente)
            if (posA != null && posA.getX() == j && posA.getY() == i) {
                g.setColor(new Color(255, 0, 0, 100));
                g.fillRect(x, y, cellSize, cellSize);
            }
            
            // Case bleue (la précédente)
            if (posB != null && posB.getX() == j && posB.getY() == i) {
                g.setColor(new Color(0, 0, 255, 100));
                g.fillRect(x, y, cellSize, cellSize);
            }
        }

        // Dessiner le surlignage jaune pour le mode highlight
        if (TrafficSimulatorGUI.getPause() && highlightedCell != null && highlightedCell.x == j && highlightedCell.y == i) {
            g.setColor(new Color(255, 255, 0, 100)); // Jaune transparent
            g.fillRect(x, y, cellSize, cellSize);
        }
    }
    
    private boolean isCurve(Case currentCase, int i, int j) {
        if (currentCase.getRoad() == null) {
            return false;
        }
    
        Direction[] directions = currentCase.getRoad().getDirection();
        if (directions.length != 1) {
            return false; // Un virage à sens unique a UNE SEULE direction
        }
    
        Direction currentDirection = directions[0]; // Direction de la case actuelle
        int incomingRoads = 0;
        boolean isTurn = false;
    
        // Vérifier les cases adjacentes pour voir si UNE SEULE route y mène avec une direction différente
        if (i > 0 && cityMap.getGrid()[i - 1][j].getRoad() != null) { // Vérifier au-dessus
            Direction incomingDirection = cityMap.getGrid()[i - 1][j].getRoad().getDirection()[0];
            if (incomingDirection == Direction.DOWN) {
                incomingRoads++;
                if (incomingDirection != currentDirection) {
                    isTurn = true;
                }
            }
        }
        if (i < cityMap.getGrid().length - 1 && cityMap.getGrid()[i + 1][j].getRoad() != null) { // Vérifier en dessous
            Direction incomingDirection = cityMap.getGrid()[i + 1][j].getRoad().getDirection()[0];
            if (incomingDirection == Direction.UP) {
                incomingRoads++;
                if (incomingDirection != currentDirection) {
                    isTurn = true;
                }
            }
        }
        if (j > 0 && cityMap.getGrid()[i][j - 1].getRoad() != null) { // Vérifier à gauche
            Direction incomingDirection = cityMap.getGrid()[i][j - 1].getRoad().getDirection()[0];
            if (incomingDirection == Direction.RIGHT) {
                incomingRoads++;
                if (incomingDirection != currentDirection) {
                    isTurn = true;
                }
            }
        }
        if (j < cityMap.getGrid()[0].length - 1 && cityMap.getGrid()[i][j + 1].getRoad() != null) { // Vérifier à droite
            Direction incomingDirection = cityMap.getGrid()[i][j + 1].getRoad().getDirection()[0];
            if (incomingDirection == Direction.LEFT) {
                incomingRoads++;
                if (incomingDirection != currentDirection) {
                    isTurn = true;
                }
            }
        }
    
        // Un virage à sens unique doit avoir exactement UNE route qui mène vers lui, avec une direction différente
        return incomingRoads == 1 && isTurn;
    }

  
    public void drawMap(Graphics g) {
        // Charger les images une seule fois
        if (!imagesLoaded) {
            Image.loadImg();
            imagesLoaded = true;
        }
    
        // Dessiner seulement la partie visible pour améliorer les performances
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

    private JPanel createChangePanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        JButton maisonButton = makeButton("Mettre une maison", "maison");
        JButton routeButton = makeButton("Mettre une route/changer rotation", "route");
        JButton feuButton = makeButton("Mettre un Feu/changer de groupe", "feu");
        JButton voitureButton = makeButton("Mettre une voiture", "voiture");
        JLabel speedLabel = new JLabel("Speed", JLabel.CENTER); // Centré au-dessus du slider
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, Controle.speed);
        speedSlider.setMajorTickSpacing(2);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setInverted(true); 

        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(20, new JLabel("-")); 
        labelTable.put(2, new JLabel("+"));  
        speedSlider.setLabelTable(labelTable);

        speedSlider.addChangeListener(e -> {
            Controle.speed = speedSlider.getValue();
            Controle.modifySpeed();
        });


        // Remplacer JTextArea par un JLabel pour le texte statique
        JLabel instruct = new JLabel("<html>Pour ajouter/retirer des objets:<br>-Mettre en pause la simulation en cliquant<br>sur P<br>-appuyer sur les cases à modifier<br>-appuyer sur le changement à effectuer</html>");
        instruct.setFont(new Font("Arial", Font.PLAIN, 14));
        instruct.setForeground(Color.BLACK);
        instruct.setHorizontalAlignment(SwingConstants.CENTER);
        instruct.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        instruct.setPreferredSize(new Dimension(300, 100));
        Box box = Box.createVerticalBox();

        box.add(maisonButton);
        box.add(routeButton);
        box.add(feuButton);
        box.add(voitureButton);
        box.add(speedLabel);
        box.add(speedSlider);

        Box box2 = Box.createVerticalBox();
        box2.add(instruct);
        

        panel.add(box);
        panel.add(box2);
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
            public void mouseClicked(MouseEvent e){
                int x = e.getY()/cellSize;
                int y = e.getX()/cellSize;
                if (pause){
                    Controle.caseUpdate.add(new Position(y, x));
                    selectedCells.add(new Position(y, x)); // Ajoutez la position à la pile
                }
                mapPanel.repaint();
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
            CityMap loadedMap = GameLoader.loadGame(selectedSave, stats);

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
                        if (!TrafficSimulatorGUI.pause){
                            TrafficSimulatorGUI.pause();
                        }else{
                            TrafficSimulatorGUI.start();
                            selectedCells.clear();
                            mapPanel.repaint();
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
                    if (controlPanel != null){
                        UpdateStats.update(controlPanel.getComponents(), stats);
                        controlPanel.repaint();
                        stats.resetMoved();
                    }
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
                if (pause && !Controle.caseUpdate.isEmpty()){
                    Controle.setChange(s2);
                    selectedCells.clear(); // Effacer les sélections
                    mapPanel.repaint();
                }
                mapPanel.requestFocus();
            }
        });
        return button;
    }
    
    public static void setUpdate(boolean b){
        needUpdate = b;
    }

    public static boolean getPause(){
        return pause;
    }

    public static void pause(){
        pause = true;
    }

    public static void start(){
        pause = false;
    }

    public void maxDezoom(){
        for(int i = 0; i<6; i++){
            zoom(false, null);
        }
    }


}