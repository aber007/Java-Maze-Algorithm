import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class Test {
    private static int CELL_SIZE = 30; // Initial size of each grid cell
    private static final int MIN_CELL_SIZE = 1; // Minimum cell size
    private static final int MAX_CELL_SIZE = 100; // Maximum cell size
    private static int xCord = 150;  // Number of rows
    private static int yCord = 150;  // Number of columns
    private static boolean[][] horizontalWalls; // Tracks horizontal walls
    private static boolean[][] verticalWalls;   // Tracks vertical walls
    private static int[] currentLocation = {0, 0}; // Player's starting location
    private static int cameraX = 0;  // Camera X offset
    private static int cameraY = 0;  // Camera Y offset
    private static int cameraSpeed = 10;  // Speed of camera movement
    private static boolean centerCameraOnPlayer = false;  // Toggles camera centering

    // Algorithm-related data
    public static String[] possibleDirections = new String[4];
    public static boolean algorithmStatus = false;
    public static ArrayList<String> backtrackLocation = new ArrayList<>();
    public static boolean backTrackStatus = false;

    public static void main(String[] args) {
        // Initialize wall data structures
        horizontalWalls = new boolean[xCord][yCord];
        verticalWalls = new boolean[xCord][yCord];
        HashMap<String, String> gridHasAllWalls = new HashMap<>();

        // Create walls for testing
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord - 1; j++) {
                horizontalWalls[i][j] = true;  // Add horizontal walls between cells
                gridHasAllWalls.put(String.format("%03d", i) + "," + String.format("%03d", j), "TRUE");
            }
        }
        for (int i = 0; i < xCord - 1; i++) {
            for (int j = 0; j < yCord; j++) {
                verticalWalls[i][j] = true;  // Add vertical walls between cells
            }
        }

        // Create main window
        JFrame mainFrame = new JFrame("Maze Algorithm");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set to fullscreen
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Create custom grid panel
        GridPanel gridPanel = new GridPanel();
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        mainFrame.add(scrollPane);

        // Add key listener for movement and zooming
        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        moveNorth(currentLocation, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveSouth(currentLocation, yCord, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_LEFT:
                        moveWest(currentLocation, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveEast(currentLocation, xCord, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_E:  // Toggle camera centering
                        toggleCameraCentering();
                        break;
                    case KeyEvent.VK_SPACE:  // Start algorithm (press 'S' to start the maze generation algorithm)
                        startAlgorithm(currentLocation, gridHasAllWalls);
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

    // Player movement
    public static void moveNorth(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        if (currentLocation[0] > 0) {
            currentLocation[0]--;
        }
        updateLocation(currentLocation, gridHasAllWalls);
    }

    public static void moveSouth(int[] currentLocation, int yCord, HashMap<String, String> gridHasAllWalls) {
        if (currentLocation[0] < yCord - 1) {
            currentLocation[0]++;
        }
        updateLocation(currentLocation, gridHasAllWalls);
    }

    public static void moveWest(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        if (currentLocation[1] > 0) {
            currentLocation[1]--;
        }
        updateLocation(currentLocation, gridHasAllWalls);
    }

    public static void moveEast(int[] currentLocation, int xCord, HashMap<String, String> gridHasAllWalls) {
        if (currentLocation[1] < xCord - 1) {
            currentLocation[1]++;
        }
        updateLocation(currentLocation, gridHasAllWalls);
    }

    // Toggle camera centering on player
    public static void toggleCameraCentering() {
        centerCameraOnPlayer = !centerCameraOnPlayer;
        System.out.println("Camera centering is now " + (centerCameraOnPlayer ? "ON" : "OFF"));
    }

    // Custom JPanel to render the grid
    static class GridPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
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
                        if (i < xCord - 1 && verticalWalls[i][j]) {
                            // Vertical wall
                            g2d.fillRect(screenX + CELL_SIZE - 2, screenY, 2, CELL_SIZE);
                        }

                        if (j < yCord - 1 && horizontalWalls[i][j]) {
                            // Horizontal wall
                            g2d.fillRect(screenX, screenY + CELL_SIZE - 2, CELL_SIZE, 2);
                        }
                    }
                }
            }
        }
    }

    // Update player's location
    public static void updateLocation(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        checkPossibleDirections(currentLocation, gridHasAllWalls);
    }

    // Check possible directions from the current location
    public static void checkPossibleDirections(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        Arrays.fill(possibleDirections, null);

        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0] - 1) + "," + String.format("%03d", currentLocation[1])).equals("TRUE")) {
                possibleDirections[0] = "NORTH";
            }
        } catch (Exception ignored) {}
        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0] + 1) + "," + String.format("%03d", currentLocation[1])).equals("TRUE")) {
                possibleDirections[1] = "SOUTH";
            }
        } catch (Exception ignored) {}
        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1] + 1)).equals("TRUE")) {
                possibleDirections[2] = "EAST";
            }
        } catch (Exception ignored) {}
        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1] - 1)).equals("TRUE")) {
                possibleDirections[3] = "WEST";
            }
        } catch (Exception ignored) {}

        // Start the algorithm if required
    }

    public static void startAlgorithm(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        algorithmStatus = !algorithmStatus;
        System.out.println("Algorithm Status: " + (algorithmStatus ? "Running" : "Stopped"));
        if (algorithmStatus) {
            // Start the algorithm in a separate thread
            Thread algorithmThread = new Thread(() -> runAlgorithm(currentLocation, gridHasAllWalls));
            algorithmThread.start();
        }
    }

    public static void runAlgorithm(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        while (algorithmStatus) {
            String key = String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1]);
            if (!backTrackStatus) {
                backtrackLocation.add(key);
            }
            ArrayList<String> possibleDirectionsList = new ArrayList<>();
            for (String direction : possibleDirections) {
                if (direction != null) {
                    possibleDirectionsList.add(direction);
                }
            }
            try {
                Random rand = new Random();
                int randIndex = rand.nextInt(possibleDirectionsList.size());
                String directionToMove = possibleDirectionsList.get(randIndex);
                backTrackStatus = false;
                switch (directionToMove) {
                    case "NORTH":
                        moveNorth(currentLocation, gridHasAllWalls);
                        break;
                    case "SOUTH":
                        moveSouth(currentLocation, yCord, gridHasAllWalls);
                        break;
                    case "WEST":
                        moveWest(currentLocation, gridHasAllWalls);
                        break;
                    case "EAST":
                        moveEast(currentLocation, xCord, gridHasAllWalls);
                        break;
                }
            } catch (Exception e) {
                backTrackStatus = true;
                if (!backtrackLocation.isEmpty()) {
                    backtrackLocation.remove(backtrackLocation.size() - 1);
                }
                if (!backtrackLocation.isEmpty()) {
                    String[] backtrackPos = backtrackLocation.get(backtrackLocation.size() - 1).split(",");
                    currentLocation[0] = Integer.parseInt(backtrackPos[0]);
                    currentLocation[1] = Integer.parseInt(backtrackPos[1]);
                }
            }
        }
    }
}
