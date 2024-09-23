import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class MainTwo {
    //User inputs
    private static boolean liveUpdate = true; // If the maze should update in real time
    private static int CELL_SIZE = 30; // Initial size of each grid cell
    private static final int MIN_CELL_SIZE = 1; // Minimum cell size
    private static final int MAX_CELL_SIZE = 500; // Maximum cell size
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
    public static boolean analogSolverMode = false;



    //A star algorithm
    public static HashMap<String,Boolean> openCells = new HashMap<>();
    public static HashMap<String,Boolean> closedCells = new HashMap<>();
    public static int[] startCell = {0, 0};
    public static int[] endCell = new int[2];
    public static int[] lastIntersection = null;




    public static void main(String[] args) {
        // Add values to lists

        System.out.println("Adding Values to Lists");
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord; j++) {
                locationsToAvoid.put(i + "." + j, true);
            }
        }
        locationsToAvoid.put("0.0", false);

        // Initialize wall data structures
        System.out.println("Initiating Data Structures");
        horizontalWalls = new boolean[xCord][yCord];
        verticalWalls = new boolean[xCord][yCord];

        // Create walls for testing
        System.out.println("Creating walls");
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

        System.out.println("Creating Frame");
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

        // Solver Mode
        JCheckBox analogSolverModeCheckbox = new JCheckBox("Analog Solver", analogSolverMode);
        analogSolverModeCheckbox.setForeground(Color.BLACK);
        analogSolverModeCheckbox.setBounds(30, 350, 150, 20);
        overlayBg.add(analogSolverModeCheckbox);


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
            startAlgorithm(analogSolverModeCheckbox, mainFrame);
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

        JButton aStarAlgorithmButton = new JButton("A* Algorithm");
        aStarAlgorithmButton.setBackground(Color.GREEN);
        aStarAlgorithmButton.setBounds(30, 390, 100, 30);
        aStarAlgorithmButton.addActionListener(e -> {
            aStarAlgorithm(mainFrame);
            mainFrame.requestFocusInWindow();
        });
        overlayBg.add(aStarAlgorithmButton);


        // Open/Close Overlay Button
        System.out.println("Loading Overlays");
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

        analogSolverModeCheckbox.addActionListener(e -> {
            analogSolverMode = analogSolverModeCheckbox.isSelected();
            algorithmStatus = false;
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

        //Init A* algorithm

        System.out.println("Initiating A* Algorithm");
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord; j++) {
                openCells.put(i + "." + j, true);
            }
        }
        endCell[0] = xCord-1;
        endCell[1] = yCord-1;
        openCells.put("0.0", false);

        System.out.println("Done");



        // Add key listener for movement and zooming
        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        moveNorth(mainFrame);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveSouth(mainFrame);
                        break;
                    case KeyEvent.VK_LEFT:
                        moveWest(mainFrame);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveEast(mainFrame);
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
                        startAlgorithm(analogSolverModeCheckbox, mainFrame);
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
        openCells.clear();
        closedCells.clear();
        main(null);


    }

    // Player movement
    public static void moveNorth(JFrame mainFrame) {
        if (currentLocation[0] > 0) {
            // Remove horizontal wall if exists
            if (!analogSolverMode) {
                if (horizontalWalls[currentLocation[0] - 1][currentLocation[1]]) {
                    horizontalWalls[currentLocation[0] - 1][currentLocation[1]] = false;
                }
                currentLocation[0]--;
                locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
            }
            else{
                if (!horizontalWalls[currentLocation[0] - 1][currentLocation[1]]){
                    currentLocation[0]--;
                }
            }
            if (analogSolverMode && currentLocation[0] == xCord-1 && currentLocation[1] == yCord-1){
                JOptionPane.showMessageDialog(mainFrame, "You did it :)");

            }
        }
    }

    public static void moveSouth(JFrame mainFrame) {
        if (currentLocation[0] < xCord - 1) {
            // Remove horizontal wall if exists
            if (!analogSolverMode) {
                if (horizontalWalls[currentLocation[0]][currentLocation[1]]) {
                    horizontalWalls[currentLocation[0]][currentLocation[1]] = false;
                }
                currentLocation[0]++;
                locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
            }
            else{
                if (!horizontalWalls[currentLocation[0]][currentLocation[1]]){
                    currentLocation[0]++;
                }
            }
            if (analogSolverMode && currentLocation[0] == xCord-1 && currentLocation[1] == yCord-1){
                JOptionPane.showMessageDialog(mainFrame, "You did it :)");
            }
        }
    }

    public static void moveWest(JFrame mainFrame) {
        if (currentLocation[1] > 0) {
            // Remove vertical wall if exists
            if (!analogSolverMode) {
                if (verticalWalls[currentLocation[0]][currentLocation[1] - 1]) {
                    verticalWalls[currentLocation[0]][currentLocation[1] - 1] = false;
                }
                currentLocation[1]--;
                locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
            }
            else{
                if (!verticalWalls[currentLocation[0]][currentLocation[1] - 1]){
                    currentLocation[1]--;
                }
            }
            if (analogSolverMode && currentLocation[0] == xCord-1 && currentLocation[1] == yCord-1){
                JOptionPane.showMessageDialog(mainFrame, "You did it :)");
            }
        }
    }

    public static void moveEast(JFrame mainFrame) {
        if (currentLocation[1] < yCord - 1) {
            // Remove vertical wall if exists
            if (!analogSolverMode) {
                if (verticalWalls[currentLocation[0]][currentLocation[1]]) {
                    verticalWalls[currentLocation[0]][currentLocation[1]] = false;
                }
                currentLocation[1]++;
                locationsToAvoid.put(currentLocation[0] + "." + currentLocation[1], false);
            }
            else{
                if (!verticalWalls[currentLocation[0]][currentLocation[1]]){
                    currentLocation[1]++;
                }
            }
            if (analogSolverMode && currentLocation[0] == xCord-1 && currentLocation[1] == yCord-1){
                JOptionPane.showMessageDialog(mainFrame, "You did it :)");
            }
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
    public static void startAlgorithm(JCheckBox analogSolverModeCheckbox, JFrame mainFrame) {
        //check nearby cells if they exist and have not been visited
        algorithmStatus = !algorithmStatus;
        analogSolverMode = false;
        analogSolverModeCheckbox.setSelected(false);

        Thread algorithmThread = new Thread(() -> algorithm(mainFrame));
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

    public static void algorithm(JFrame mainFrame) {
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
                        moveNorth(mainFrame);
                        break;
                    case "SOUTH":
                        moveSouth(mainFrame);
                        break;
                    case "WEST":
                        moveWest(mainFrame);
                        break;
                    case "EAST":
                        moveEast(mainFrame);
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

    public static void aStarAlgorithm(JFrame mainFrame) {
        double northCost = 999999;
        double southCost = 999999;
        double eastCost = 999999;
        double westCost = 999999;
        int[] northCell = {currentLocation[0] - 1, currentLocation[1]};
        int[] southCell = {currentLocation[0] + 1, currentLocation[1]};
        int[] eastCell = {currentLocation[0], currentLocation[1]+1};
        int[] westCell = {currentLocation[0], currentLocation[1]-1};
        if (currentLocation != endCell) {

            //Find possible moves


            ArrayList<String> possibleAStarMoves = new ArrayList();
            if (currentLocation[1] < yCord - 1) {
                if (!verticalWalls[currentLocation[0]][currentLocation[1]]) {
                    possibleAStarMoves.add("EAST");
                }
            }
            if (currentLocation[1] > 0) {
                if (!verticalWalls[currentLocation[0]][currentLocation[1] - 1]) {
                    possibleAStarMoves.add("WEST");
                }
            }
            if (currentLocation[0] < xCord - 1) {
                if (!horizontalWalls[currentLocation[0]][currentLocation[1]]) {
                    possibleAStarMoves.add("SOUTH");
                }
            }
            if (currentLocation[0] > 0) {
                if (!horizontalWalls[currentLocation[0] - 1][currentLocation[1]]) {
                    possibleAStarMoves.add("NORTH");
                }
            }
            System.out.println(possibleAStarMoves);
            if (possibleAStarMoves.size() > 2) {
                lastIntersection = Arrays.copyOf(currentLocation, currentLocation.length);
            }
            System.out.println(Arrays.toString(lastIntersection));
            System.out.println("possible Locations: " + possibleAStarMoves);

            // Determine cost of movement


            for (String direction : possibleAStarMoves) {
                switch (direction) {
                    case "NORTH":
                        if (openCells.get(northCell[0] + "." + northCell[1])) {
                            northCost = Math.sqrt(Math.pow(endCell[0] - northCell[0], 2) + Math.pow(endCell[1] - northCell[1], 2));
                        }
                        break;
                    case "SOUTH":
                        if (openCells.get(southCell[0] + "." + southCell[1])) {
                            southCost = Math.sqrt(Math.pow(endCell[0] - southCell[0], 2) + Math.pow(endCell[1] - southCell[1], 2));
                        }
                        break;
                    case "EAST":
                        if (openCells.get(eastCell[0] + "." + eastCell[1])) {
                            eastCost = Math.sqrt(Math.pow(endCell[0] - eastCell[0], 2) + Math.pow(endCell[1] - eastCell[1], 2));
                        }
                        break;
                    case "WEST":
                        if (openCells.get(westCell[0] + "." + westCell[1])) {
                            westCost = Math.sqrt(Math.pow(endCell[0] - westCell[0], 2) + Math.pow(endCell[1] - westCell[1], 2));
                        }
                        break;
                }
            }
            System.out.println("Current Cell: " + Arrays.toString(currentLocation));
            System.out.println("North Cell: " + Arrays.toString(northCell));
            System.out.println("South Cell: " + Arrays.toString(southCell));
            System.out.println("East Cell: " + Arrays.toString(eastCell));
            System.out.println("West Cell: " + Arrays.toString(westCell));
            System.out.println();
            System.out.println("North Cost: " + northCost);
            System.out.println("South Cost: " + southCost);
            System.out.println("East Cost: " + eastCost);
            System.out.println("West Cost: " + westCost);

            double minValue1 = Math.min(northCost, southCost);
            double minValue2 = Math.min(eastCost, westCost);
            double minValue = Math.min(minValue1, minValue2);

            String lowestF = null;
            if (northCost == minValue) {
                lowestF = "NORTH";
            } else if (southCost == minValue) {
                lowestF = "SOUTH";
            } else if (eastCost == minValue) {
                lowestF = "EAST";
            } else if (westCost == minValue) {
                lowestF = "WEST";
            }
            System.out.println("Lowest F: " + lowestF);

            //Add lowestF to closedList
            if (lowestF == ("NORTH")) {
                openCells.put(northCell[0] + "." + northCell[1], false);
                moveNorth(mainFrame);
                mainFrame.repaint();
            } else if (lowestF == ("SOUTH")) {
                openCells.put(southCell[0] + "." + southCell[1], false);
                moveSouth(mainFrame);
                mainFrame.repaint();
            } else if (lowestF == ("EAST")) {
                openCells.put(eastCell[0] + "." + eastCell[1], false);
                moveEast(mainFrame);
                mainFrame.repaint();
            } else if (lowestF == ("WEST")) {
                openCells.put(westCell[0] + "." + westCell[1], false);
                moveWest(mainFrame);
                mainFrame.repaint();
            }
        }
        else{
            System.out.println("Maze Solved");
        }




    }
}
