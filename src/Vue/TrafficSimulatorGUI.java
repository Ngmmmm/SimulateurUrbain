package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficSimulatorGUI extends JFrame {

    private static final int ROWS = 100;
    private static final int COLS = 100;
    private JPanel mapPanel;
    private JScrollPane scrollPane;
    private int cellSize = 40;
    private double zoomFactor = 1.0;

    private List<Car> cars = new ArrayList<>();
    private Timer moveCarTimer;
    private Timer animationTimer;
    private Point lastDragPoint;
    private boolean isPaused = false;

    public TrafficSimulatorGUI() {
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

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        mapPanel.addMouseWheelListener(createMouseWheelZoomListener());
        mapPanel.addMouseListener(createMouseDragListener());
        mapPanel.addMouseMotionListener(createMouseMotionListener());

        initializeCars(5); // Ajouter 5 voitures au démarrage
        startCarMovement();
        startAnimation();

        setVisible(true);
    }

    private void initializeCars(int numberOfCars) {
        Random random = new Random();
        for (int i = 0; i < numberOfCars; i++) {
            int startX = random.nextInt(COLS);
            int startY = random.nextInt(ROWS);
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            cars.add(new Car(startX, startY, color));
        }
    }

    private void startCarMovement() {
        moveCarTimer = new Timer(500, e -> {
            if (!isPaused) {
                for (Car car : cars) {
                    car.moveRandomly();
                }
            }
        });
        moveCarTimer.start();
    }

    private void startAnimation() {
        animationTimer = new Timer(16, e -> {
            if (!isPaused) {
                for (Car car : cars) {
                    car.animate();
                }
                mapPanel.repaint();
            }
        });
        animationTimer.start();
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
        JMenuItem pauseItem = new JMenuItem("Pause");

        startItem.addActionListener(e -> isPaused = false);
        stopItem.addActionListener(e -> resetSimulation());
        pauseItem.addActionListener(e -> isPaused = !isPaused);

        simulationMenu.add(startItem);
        simulationMenu.add(stopItem);
        simulationMenu.add(pauseItem);
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
        panel.setLayout(new GridLayout(1, 4));

        JButton addCarButton = new JButton("Ajouter une voiture");
        JButton resetButton = new JButton("Réinitialiser");
        JLabel speedLabel = new JLabel("Vitesse: 50%");

        addCarButton.addActionListener(e -> {
            Random random = new Random();
            int startX = random.nextInt(COLS);
            int startY = random.nextInt(ROWS);
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            cars.add(new Car(startX, startY, color));
        });

        resetButton.addActionListener(e -> resetSimulation());


        panel.add(addCarButton);
        panel.add(resetButton);

        panel.add(speedLabel);

        panel.setBorder(BorderFactory.createTitledBorder("Contrôles"));
        return panel;
    }

    private void resetSimulation() {
        cars.clear();
        initializeCars(5);
    }

    private JPanel createMapPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
                for (Car car : cars) {
                    car.draw(g, cellSize);
                }
            }
        };
        panel.setPreferredSize(new Dimension(COLS * cellSize, ROWS * cellSize));
        return panel;
    }

    private void drawMap(Graphics g) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = j * cellSize;
                int y = i * cellSize;

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, cellSize, cellSize);

                g.setColor(Color.GRAY);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
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
        SwingUtilities.invokeLater(TrafficSimulatorGUI::new);
    }

    private static class Car {
        private int x, y;
        private float xPos, yPos;
        private int targetX, targetY;
        private Color color;
        private float speed = 0.1f;

        public Car(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.xPos = x;
            this.yPos = y;
            this.targetX = x;
            this.targetY = y;
            this.color = color;
        }

        public void moveRandomly() {
            Random random = new Random();
            int direction = random.nextInt(4);

            switch (direction) {
                case 0 -> { if (x > 0) targetX--; }
                case 1 -> { if (x < COLS - 1) targetX++; }
                case 2 -> { if (y > 0) targetY--; }
                case 3 -> { if (y < ROWS - 1) targetY++; }
            }
        }

        public void animate() {
            xPos += (targetX - xPos) * speed;
            yPos += (targetY - yPos) * speed;

            if (Math.abs(xPos - targetX) < 0.1 && Math.abs(yPos - targetY) < 0.1) {
                x = targetX;
                y = targetY;
            }
        }

        public void draw(Graphics g, int cellSize) {
            g.setColor(color);
            g.fillRect((int) (xPos * cellSize), (int) (yPos * cellSize), cellSize, cellSize);
        }


    }
}