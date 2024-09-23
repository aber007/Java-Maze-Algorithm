import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MainTwo {
    //User inputs
    private static boolean liveUpdate = true; // If the maze should update in real time
    private static int CELL_SIZE = 30; // Initial size of each grid cell
    private static final int MIN_CELL_SIZE = 1; // Minimum cell size
    private static final int MAX_CELL_SIZE = 100; // Maximum cell size
    private static int xCord = 100;  // Number of rows
    private static int yCord = 100;  // Number of columns
    private static int[] currentLocation = {0, 0}; // Player's starting location

    // Do not change
    private static boolean[][] horizontalWalls; // Tracks horizontal walls
    private static boolean[][] verticalWalls;   // Tracks vertical walls
    private static int cameraX = 0;  // Camera X offset
    private static int cameraY = 0;  // Camera Y offset
    private static final int cameraSpeed = 10;  // Speed of camera movement
    private static boolean centerCameraOnPlayer = false;  // Toggles camera centering
    private static final HashMap<String, Boolean> locationsToAvoid = new HashMap<>();
    private static ArrayList<String> backTrack = new ArrayList<>();
    private static final ArrayList<String> possibleMoves = new ArrayList<>();
    private static boolean algorithmStatus = false;
    public static boolean backTrackStatus = false;
    public static GridPanel gridPanel = new GridPanel();
    public static long start;
    public static boolean overlayToggle = false;


    public static void main(String[] args) {
        // Add values to lists
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord; j++) {
                locationsToAvoid.put(i + "." + j, true);
            }
        }
        locationsToAvoid.put("0.0", false);

        // Initialize wall data structures
        horizontalWalls = new boolean[xCord][yCord];
        verticalWalls = new boolean[xCord][yCord];

        // Create walls for testing
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord; j++) {
                horizontalWalls[i][j] = true;  // Add horizontal walls between cells
            }
        }
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord; j++) {
                verticalWalls[i][j] = true;  // Add vertical walls between cells
            }
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        JFrame mainFrame = new JFrame("Maze Algorithm");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        JLayeredPane mainLayers = new JLayeredPane();
        mainLayers.setLayout(null);
        mainLayers.setBounds(0, 0, (int) width, (int) height);
        mainFrame.add(mainLayers);

        JScrollPane scrollPane = new JScrollPane(gridPanel); // Placeholder for gridPanel
        scrollPane.setBounds(0, 0, (int) width, (int) height);
        mainLayers.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        // Overlay panel
        JPanel overlayBg = new JPanel();
        overlayBg.setBounds((int) width - 200, 0, 200, (int) height);
        overlayBg.setBackground(Color.BLACK);
        overlayBg.setLayout(null);
        mainLayers.add(overlayBg, JLayeredPane.PALETTE_LAYER);
        overlayBg.setVisible(true);

        // Live Update Checkbox
        JCheckBox liveUpdateCheckbox = new JCheckBox("Live Update", liveUpdate);
        liveUpdateCheckbox.setForeground(Color.BLACK);
        liveUpdateCheckbox.setBounds(30, 200, 150, 20);
        overlayBg.add(liveUpdateCheckbox);

        // X Coordinate Input
        JLabel gridSize = new JLabel("Grid Size:");
        gridSize.setForeground(Color.WHITE);
        gridSize.setBounds(30, 230, 150, 20);
        overlayBg.add(gridSize);

        JTextField sizeInput = new JTextField(String.valueOf(xCord));
        sizeInput.setBounds(30, 260, 150, 30);
        overlayBg.add(sizeInput);


        // Buttons
        JButton start = new JButton("Start");
        start.setBounds(30, 30, 100, 30);
        start.addActionListener(e -> {
            startAlgorithm();
            mainFrame.requestFocusInWindow();
        });
        start.setBackground(Color.GREEN);
        overlayBg.add(start);

        JButton reset = new JButton("Reset (WIP)");
        reset.setBounds(30, 70, 100, 30);
        reset.addActionListener(e -> {
            resetApp(mainFrame);
            mainFrame.requestFocusInWindow();
        });
        reset.setBackground(Color.ORANGE);
        overlayBg.add(reset);

        // Open/Close Overlay Button
        ImageIcon overlayIcon = new ImageIcon("Images/image1.png");
        JButton overlayButton = new JButton("", overlayIcon);
        overlayButton.setOpaque(false);
        overlayButton.setContentAreaFilled(false);
        overlayButton.setBounds((int) width - 50, 0, 50, 50);
        overlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                overlayBg.setVisible(!overlayBg.isVisible());
                mainFrame.requestFocusInWindow();
            }
        });
        mainLayers.add(overlayButton, JLayeredPane.MODAL_LAYER);
        overlayButton.setVisible(true);


        liveUpdateCheckbox.addActionListener(e -> {
            liveUpdate = liveUpdateCheckbox.isSelected();
            mainFrame.requestFocusInWindow();
        });

        // Action listener for X Coordinate input
        sizeInput.addActionListener(e -> {
            try {
                xCord = Integer.parseInt(sizeInput.getText());
                yCord = Integer.parseInt(sizeInput.getText());
                resetApp(mainFrame);
                if (xCord <= 0) throw new NumberFormatException(); // Ensure positive value
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid X coordinate. Please enter a positive integer.");
            }
            mainFrame.requestFocusInWindow();
        });



        // Add key listener for movement and zooming
        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        moveNorth();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveSouth();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveWest();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveEast();
                        break;
                    case KeyEvent.VK_PLUS:  // Zoom in (+ key)
                    case KeyEvent.VK_EQUALS:  // Zoom in (on some keyboards, + is the same as = key)
                        zoomIn();
                        break;
                    case KeyEvent.VK_MINUS:  // Zoom out (- key)
                        zoomOut();
                        break;
                    case KeyEvent.VK_W:  // Move camera up
                        if (!centerCameraOnPlayer) moveCameraUp();
                        break;
                    case KeyEvent.VK_A:  // Move camera left
                        if (!centerCameraOnPlayer) moveCameraLeft();
                        break;
                    case KeyEvent.VK_S:  // Move camera down
                        if (!centerCameraOnPlayer) moveCameraDown();
                        break;
                    case KeyEvent.VK_D:  // Move camera right
                        if (!centerCameraOnPlayer) moveCameraRight();
                        break;
                    case KeyEvent.VK_E:  // Toggle camera centering
                        toggleCameraCentering();
                        break;
                    case KeyEvent.VK_SPACE:
                        startAlgorithm();
                        break;

                }
                gridPanel.repaint(); // Repaint the grid after moving or zooming
            }


            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        });
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setFocusable(true);
        mainFrame.requestFocusInWindow();
    }

    private static void resetApp(JFrame mainFrame) {
        mainFrame.dispose();
        backTrack.clear();
        backTrack.add("0.0");
        currentLocation[0] = 0;
        currentLocation[1] = 0;
        main(null);


    }

    // Player movement
    public static void moveNorth() {
        if (currentLocation[0] > 0) {
            // Remove horizontal wall if exists
            if (horizontalWalls[currentLocation[0] - 1][currentLocation[1]]) {
                horizontalWalls[currentLocation[0] - 1][currentLocation[1]] = false;
            }
            currentLocation[0]--;
            locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
        }
    }

    public static void moveSouth() {
        if (currentLocation[0] < xCord - 1) {
            // Remove horizontal wall if exists
            if (horizontalWalls[currentLocation[0]][currentLocation[1]]) {
                horizontalWalls[currentLocation[0]][currentLocation[1]] = false;
            }
            currentLocation[0]++;
            locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
        }
    }

    public static void moveWest() {
        if (currentLocation[1] > 0) {
            // Remove vertical wall if exists
            if (verticalWalls[currentLocation[0]][currentLocation[1] - 1]) {
                verticalWalls[currentLocation[0]][currentLocation[1] - 1] = false;
            }
            currentLocation[1]--;
            locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
        }
    }

    public static void moveEast() {
        if (currentLocation[1] < yCord - 1) {
            // Remove vertical wall if exists
            if (verticalWalls[currentLocation[0]][currentLocation[1]]) {
                verticalWalls[currentLocation[0]][currentLocation[1]] = false;
            }
            currentLocation[1]++;
            locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
        }
    }

    // Zoom functionality
    public static void zoomIn() {
        if (CELL_SIZE < MAX_CELL_SIZE) {
            CELL_SIZE += 1;  // Increase the cell size for zoom in
            System.out.println("Zooming in: New cell size = " + CELL_SIZE);
        }
    }

    public static void zoomOut() {
        if (CELL_SIZE > MIN_CELL_SIZE) {
            CELL_SIZE -= 1;  // Decrease the cell size for zoom out
            System.out.println("Zooming out: New cell size = " + CELL_SIZE);
        }
    }

    // Camera movement
    public static void moveCameraUp() {
        cameraY = Math.max(0, cameraY - cameraSpeed);  // Move the camera up
        System.out.println("Camera moved up. New cameraY: " + cameraY);
    }

    public static void moveCameraDown() {
        cameraY = Math.min(yCord - 1, cameraY + cameraSpeed);  // Move the camera down
        System.out.println("Camera moved down. New cameraY: " + cameraY);
    }

    public static void moveCameraLeft() {
        cameraX = Math.max(0, cameraX - cameraSpeed);  // Move the camera left
        System.out.println("Camera moved left. New cameraX: " + cameraX);
    }

    public static void moveCameraRight() {
        cameraX = Math.min(xCord - 1, cameraX + cameraSpeed);  // Move the camera right
        System.out.println("Camera moved right. New cameraX: " + cameraX);
    }

    // Toggle camera centering on player
    public static void toggleCameraCentering() {
        centerCameraOnPlayer = !centerCameraOnPlayer;
        System.out.println("Camera centering is now " + (centerCameraOnPlayer ? "ON" : "OFF"));
    }

    // Main Algorithm
    public static void startAlgorithm() {
        //check nearby cells if they exist and have not been visited
        algorithmStatus = !algorithmStatus;

        Thread algorithmThread = new Thread(MainTwo::algorithm);
        algorithmThread.start(); // Start the thread
        start = System.currentTimeMillis();
        Thread timer = new Thread(MainTwo::timerThread);
        timer.start();
    }
    public static void timerThread() {
        start = System.currentTimeMillis();

        while (algorithmStatus){
            int progress = 0;
            System.out.print("\033[H\033[2J");
            System.out.flush();
            for (Boolean value : locationsToAvoid.values()) {
                if (Boolean.FALSE.equals(value)) {
                    progress++;
                }
            }
            float progressF = (float) progress / locationsToAvoid.size();
        }
    }

    public static void algorithm() {
        while (algorithmStatus) {
            if (liveUpdate) {
                SwingUtilities.invokeLater(gridPanel::repaint);
            }
            possibleMoves.clear();
            try {
                if (currentLocation[0] != 0) {
                    if (locationsToAvoid.get(currentLocation[0] - 1 + "." + currentLocation[1])) {
                        possibleMoves.add("NORTH");
                    }
                }
            } catch (Exception e) {}
            try {
                if (currentLocation[0] != yCord - 1) {
                    if (locationsToAvoid.get(currentLocation[0] + 1 + "." + currentLocation[1])) {
                        possibleMoves.add("SOUTH");
                    }
                }
            }catch (Exception e) {}
            try {
                if (currentLocation[1] != 0) {
                    if (locationsToAvoid.get(currentLocation[0] + "." + (currentLocation[1] - 1))) {
                        possibleMoves.add("WEST");
                    }
                }
            } catch (Exception e) {}
            try {
                if (currentLocation[1] != xCord - 1) {
                    if (locationsToAvoid.get(currentLocation[0] + "." + (currentLocation[1] + 1))) {
                        possibleMoves.add("EAST");
                    }
                }
            } catch (Exception e) {}
            if (!backTrackStatus){
                backTrack.add(currentLocation[0] + "." + currentLocation[1]);
            }
            if (possibleMoves.isEmpty()) {
                if (currentLocation[0] == 0 && currentLocation[1] == 0 ) {
                    System.out.println("Maze Finished");
                    long time = System.currentTimeMillis() - start;
                    liveUpdate = true;
                    System.out.println("Time taken: " + time + " ms / " + time / 1000 + " seconds");
                    algorithmStatus = false;
                    gridPanel.repaint();
                }
                backTrackStatus = true;
                // Trigger backtrack here
                if (!backTrack.isEmpty()) {
                    String lastVar = backTrack.removeLast();
                    int xVal = Integer.parseInt(lastVar.split("\\.")[0]);
                    int yVal = Integer.parseInt(lastVar.split("\\.")[1]);
                    currentLocation = new int[]{xVal, yVal};
                }
            } else {
                Random rand = new Random();
                int randIndex = rand.nextInt(possibleMoves.size());
                String directionToMove = possibleMoves.get(randIndex);
                backTrackStatus = false;
                switch (directionToMove) {
                    case "NORTH":
                        moveNorth();
                        break;
                    case "SOUTH":
                        moveSouth();
                        break;
                    case "WEST":
                        moveWest();
                        break;
                    case "EAST":
                        moveEast();
                        break;
                }
            }
        }
    }


    public static void toggleOverlay(JPanel overlayBg) {
        overlayToggle = !overlayToggle;
        System.out.println("Overlay " + (overlayToggle ? "ON" : "OFF"));
        overlayBg.setVisible(overlayToggle);
    }
    // Custom JPanel to render the grid
    static class GridPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            boolean visualize = true;
            if (visualize) {

                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Update camera position if centering on player
                if (centerCameraOnPlayer) {
                    cameraX = currentLocation[1] - getWidth() / (2 * CELL_SIZE);
                    cameraY = currentLocation[0] - getHeight() / (2 * CELL_SIZE);
                    cameraX = Math.max(0, Math.min(xCord - 1, cameraX));  // Clamp camera position
                    cameraY = Math.max(0, Math.min(yCord - 1, cameraY));  // Clamp camera position
                }

                // Draw grid cells and walls with camera offset
                for (int i = 0; i < xCord; i++) {
                    for (int j = 0; j < yCord; j++) {

                        int screenX = (j - cameraX) * CELL_SIZE;
                        int screenY = (i - cameraY) * CELL_SIZE;

                        // Only draw visible cells
                        if (screenX + CELL_SIZE > 0 && screenY + CELL_SIZE > 0 &&
                                screenX < getWidth() && screenY < getHeight()) {

                            // Draw the cell background
                            g2d.setColor(Color.GRAY);
                            g2d.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);

                            // Draw the player
                            if (i == currentLocation[0] && j == currentLocation[1]) {
                                g2d.setColor(Color.BLUE);
                                g2d.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                            }

                            // Draw walls
                            g2d.setColor(Color.BLACK);

                            // Vertical walls (including rightmost vertical wall at the far right of the grid)
                            if (j < yCord - 1 && verticalWalls[i][j]) {
                                g2d.fillRect(screenX + CELL_SIZE - 2, screenY, 2, CELL_SIZE);
                            }

                            // Horizontal walls (including bottom horizontal wall at the bottom of the grid)
                            if (i < xCord - 1 && horizontalWalls[i][j]) {
                                g2d.fillRect(screenX, screenY + CELL_SIZE - 2, CELL_SIZE, 2);
                            }

                        }
                    }
                }
            }
        }
    }
}
