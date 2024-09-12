import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class MainTwo {
    private static int CELL_SIZE = 30; // Initial size of each grid cell
    private static final int MIN_CELL_SIZE = 1; // Minimum cell size
    private static final int MAX_CELL_SIZE = 100; // Maximum cell size
    private static int xCord = 10;  // Number of rows
    private static int yCord = 10;  // Number of columns
    private static boolean[][] horizontalWalls; // Tracks horizontal walls
    private static boolean[][] verticalWalls;   // Tracks vertical walls
    private static int[] currentLocation = {0, 0}; // Player's starting location
    private static int cameraX = 0;  // Camera X offset
    private static int cameraY = 0;  // Camera Y offset
    private static int cameraSpeed = 10;  // Speed of camera movement
    private static boolean centerCameraOnPlayer = false;  // Toggles camera centering
    private static ArrayList locationsToAvoid = new ArrayList();
    private static ArrayList backTrack = new ArrayList();

    public static void main(String[] args) {
        // Add values to lists
        locationsToAvoid.add("0.0");

        // Initialize wall data structures
        horizontalWalls = new boolean[xCord][yCord];
        verticalWalls = new boolean[xCord][yCord];

        // Create walls for testing
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord - 1; j++) {
                horizontalWalls[i][j] = true;  // Add horizontal walls between cells
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

    // Player movement
    public static void moveNorth() {
        if (currentLocation[0] > 0) {
            // Remove horizontal wall if exists
            if (horizontalWalls[currentLocation[0] - 1][currentLocation[1]]) {
                horizontalWalls[currentLocation[0] - 1][currentLocation[1]] = false;
            }
            currentLocation[0]--;
            locationsToAvoid.add(currentLocation[0] + "." + currentLocation[1]);
        }
    }

    public static void moveSouth() {
        if (currentLocation[0] < xCord - 1) {
            // Remove horizontal wall if exists
            if (horizontalWalls[currentLocation[0]][currentLocation[1]]) {
                horizontalWalls[currentLocation[0]][currentLocation[1]] = false;
            }
            currentLocation[0]++;
            locationsToAvoid.add(currentLocation[0] + "." + currentLocation[1]);
        }
    }

    public static void moveWest() {
        if (currentLocation[1] > 0) {
            // Remove vertical wall if exists
            if (verticalWalls[currentLocation[0]][currentLocation[1] - 1]) {
                verticalWalls[currentLocation[0]][currentLocation[1] - 1] = false;
            }
            currentLocation[1]--;
            locationsToAvoid.add(currentLocation[0] + "." + currentLocation[1]);
        }
    }

    public static void moveEast() {
        if (currentLocation[1] < yCord - 1) {
            // Remove vertical wall if exists
            if (verticalWalls[currentLocation[0]][currentLocation[1]]) {
                verticalWalls[currentLocation[0]][currentLocation[1]] = false;
            }
            currentLocation[1]++;
            locationsToAvoid.add(currentLocation[0] + "." + currentLocation[1]);
        }
    }

    // Zoom functionality
    public static void zoomIn() {
        if (CELL_SIZE < MAX_CELL_SIZE) {
            CELL_SIZE += 5;  // Increase the cell size for zoom in
            System.out.println("Zooming in: New cell size = " + CELL_SIZE);
        }
    }

    public static void zoomOut() {
        if (CELL_SIZE > MIN_CELL_SIZE) {
            CELL_SIZE -= 5;  // Decrease the cell size for zoom out
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
        String currentLocationStr = currentLocation[0] +"."+ currentLocation[1];
        System.out.println("CURRENT LOCATION " + currentLocationStr);
        System.out.println(locationsToAvoid.toString());
        if (currentLocation[0] != 0){
            if (!locationsToAvoid.contains(currentLocation[0]-1 + "." + currentLocation[1])) {
                System.out.println("CAN GO NORTH");
            }
        }
        if (currentLocation[0] != yCord - 1) {
            if (!locationsToAvoid.contains(currentLocation[0]+1 + "." + currentLocation[1])) {
                System.out.println("CAN GO SOUTH");
            }
        }
        if (currentLocation[1] != 0 ){
            if (!locationsToAvoid.contains(currentLocation[0] + "." + (currentLocation[1]-1))) {
                System.out.println("CAN GO WEST");
            }
        }
        if (currentLocation[1] != xCord - 1) {
            if (!locationsToAvoid.contains(currentLocation[0] + "." + (currentLocation[1]+1))) {
                System.out.println("CAN GO EAST");
            }
        }
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
}
