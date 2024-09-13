import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Test {
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
    private static ArrayList possibleMoves = new ArrayList();
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
