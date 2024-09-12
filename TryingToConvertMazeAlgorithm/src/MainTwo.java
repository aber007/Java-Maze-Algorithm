import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainTwo {
    private static int CELL_SIZE = 30; // Initial size of each grid cell
    private static final int MIN_CELL_SIZE = 1; // Minimum cell size
    private static final int MAX_CELL_SIZE = 100; // Maximum cell size
    private static int xCord = 1000;  // Number of rows
    private static int yCord = 1000;  // Number of columns
    private static boolean[][] horizontalWalls; // Tracks horizontal walls
    private static boolean[][] verticalWalls;   // Tracks vertical walls
    private static int[] currentLocation = {0, 0}; // Player's starting location

    public static void main(String[] args) {
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
        mainFrame.setSize(900,1200);

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

    public static void moveNorth() {
        if (currentLocation[0] > 0) {
            // Remove horizontal wall if exists
            if (horizontalWalls[currentLocation[0] - 1][currentLocation[1]]) {
                horizontalWalls[currentLocation[0] - 1][currentLocation[1]] = false;
            }
            currentLocation[0]--;
        }
    }

    public static void moveSouth() {
        if (currentLocation[0] < xCord - 1) {
            // Remove horizontal wall if exists
            if (horizontalWalls[currentLocation[0]][currentLocation[1]]) {
                horizontalWalls[currentLocation[0]][currentLocation[1]] = false;
            }
            currentLocation[0]++;
        }
    }

    public static void moveWest() {
        if (currentLocation[1] > 0) {
            // Remove vertical wall if exists
            if (verticalWalls[currentLocation[0]][currentLocation[1] - 1]) {
                verticalWalls[currentLocation[0]][currentLocation[1] - 1] = false;
            }
            currentLocation[1]--;
        }
    }

    public static void moveEast() {
        if (currentLocation[1] < yCord - 1) {
            // Remove vertical wall if exists
            if (verticalWalls[currentLocation[0]][currentLocation[1]]) {
                verticalWalls[currentLocation[0]][currentLocation[1]] = false;
            }
            currentLocation[1]++;
        }
    }

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

    // Custom JPanel to render the grid
    static class GridPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Draw grid cells and walls
            for (int i = 0; i < xCord; i++) {
                for (int j = 0; j < yCord; j++) {
                    // Draw the cell background
                    g2d.setColor(Color.GRAY);
                    g2d.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    // Draw the player
                    if (i == currentLocation[0] && j == currentLocation[1]) {
                        g2d.setColor(Color.BLUE);
                        g2d.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }

                    // Draw walls
                    g2d.setColor(Color.BLACK);
                    if (i < xCord - 1 && verticalWalls[i][j]) {
                        // Vertical wall
                        g2d.fillRect((j + 1) * CELL_SIZE - 2, i * CELL_SIZE, 2, CELL_SIZE);
                    } else if (i == xCord - 1) {
                        // Right edge of the last column
                        g2d.fillRect(j * CELL_SIZE + CELL_SIZE - 2, i * CELL_SIZE, 2, CELL_SIZE);
                    }

                    if (j < yCord - 1 && horizontalWalls[i][j]) {
                        // Horizontal wall
                        g2d.fillRect(j * CELL_SIZE, (i + 1) * CELL_SIZE - 2, CELL_SIZE, 2);
                    } else if (j == yCord - 1) {
                        // Bottom edge of the last row
                        g2d.fillRect(j * CELL_SIZE, i * CELL_SIZE + CELL_SIZE - 2, CELL_SIZE, 2);
                    }
                }
            }
        }
    }
}
