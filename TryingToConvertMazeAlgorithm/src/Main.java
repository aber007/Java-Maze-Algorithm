import java.util.HashMap;
import java.util.Objects;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //Create a grid
        int xCord = 10;
        int yCord = 10;
        HashMap<String, JPanel> grids = new HashMap<>();
        HashMap<String, JPanel> gridWalls = new HashMap<>();
        for (int i = 0; i<xCord; i++){
            for (int j = 0; j<yCord; j++){
                String xCordFormatted = String.format("%03d", i);
                String yCordFormatted = String.format("%03d", j);
                grids.put(xCordFormatted+","+yCordFormatted, null);
                gridWalls.put(xCordFormatted+","+yCordFormatted + "y", null);
                gridWalls.put(xCordFormatted+","+yCordFormatted + "x", null);
            }
        }
        //create main window
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Maze Algorithm");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200,900);

        mainFrame.setLayout(null);

        // Create gridPanel with GridLayout to automatically manage the grid
        JPanel gridPanel = new JPanel();
        gridPanel.setBounds(0, 0, 780, 780);
        gridPanel.setBackground(Color.WHITE);
        System.out.println("gridSize:" + xCord +","+ yCord);
        gridPanel.setLayout(new GridLayout(xCord, yCord, 0, 0));  // 5x5 grid with 2px gaps

        mainFrame.add(gridPanel);
        mainFrame.setVisible(true);
        int verticalGridSize = 780/yCord;
        int horizontalGridSize = 780/xCord;

        // Create the grids
        for (int i = 0; i < xCord; i++) {
            for (int j = 0; j < yCord; j++) {
                String key = String.format("%03d"+","+"%03d", i, j);
                String[] keySplit = key.split(",");
                // Create small panels to fill the grid
                JPanel smallGridPanel = new JPanel();
                smallGridPanel.setLayout(null);
                smallGridPanel.setBackground(Color.GRAY);  // Set the background color to gray

                // Store the JPanel in the HashMap with the key
                grids.put(key, smallGridPanel);

                // Add the panel to the gridPanel (which has GridLayout)
                gridPanel.add(smallGridPanel);

                // Add Horizontal and Vertical Walls to each smallGridPanel

                if (!keySplit[1].equals((String.format("%03d", xCord - 1)))){
                    JPanel gridWallVertical = new JPanel();
                    int wallWidth = (int) Math.round(horizontalGridSize * 0.1);
                    gridWallVertical.setBounds(horizontalGridSize - wallWidth, 0, wallWidth, horizontalGridSize);
                    gridWallVertical.setBackground(Color.BLACK);
                    gridWalls.put((key + "x"), gridWallVertical);
                    smallGridPanel.add(gridWallVertical);
                }
                if (!keySplit[0].equals((String.format("%03d", yCord - 1)))) {
                    JPanel gridWallHorizontal = new JPanel();
                    int wallHeight = (int) Math.round(horizontalGridSize * 0.1);
                    gridWallHorizontal.setBounds(0, verticalGridSize - wallHeight, verticalGridSize, wallHeight);
                    gridWallHorizontal.setBackground(Color.BLACK);
                    gridWalls.put((key + "y"), gridWallHorizontal);
                    smallGridPanel.add(gridWallHorizontal);
                }
            }
        }
        //check if all grids exist
        System.out.println(grids.size());
        JPanel targetPanel = grids.get("000,000");
        System.out.println(targetPanel);
        targetPanel.setBackground(Color.BLUE);

        int[] currentLocation = {0,0};
        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        moveNorth(currentLocation, grids, gridWalls);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveSouth(currentLocation, yCord, grids, gridWalls);
                        break;
                    case KeyEvent.VK_LEFT:
                        moveWest(currentLocation, grids, gridWalls);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveEast(currentLocation, xCord, grids, gridWalls);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        // Make sure the JFrame is focusable for key events
        mainFrame.setFocusable(true);
        mainFrame.requestFocusInWindow();
    }

    public static void moveEast(int[] currentLocation, int xCord ,HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls) {
        System.out.println("E");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[1] < (xCord-1)) {
            currentLocation[1]++;
        }
        update(lastLocation,currentLocation, grids, gridWalls);



    }

    public static void moveWest(int[] currentLocation, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls) {
        System.out.println("W");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[1] > 0){
            currentLocation[1]--;
        }
        update(lastLocation,currentLocation, grids, gridWalls);



    }

    public static void moveNorth(int[] currentLocation, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls) {
        System.out.println("N");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[0] > 0){
            currentLocation[0]--;
        }
        update(lastLocation,currentLocation, grids, gridWalls);

    }

    public static void moveSouth(int[] currentLocation, int yCord, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls) {
        System.out.println("S");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[0] < (yCord-1)){
            currentLocation[0]++;
        }
        update(lastLocation,currentLocation, grids, gridWalls);

    }
    public static void update(int[] lastLocation, int[] currentLocation, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls) {
        // Initial variable creation
        String currentLocationKey = String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1]);
        String lastLocationKey = String.format("%03d", lastLocation[0]) + "," + String.format("%03d", lastLocation[1]);
        System.out.println("IN UPDATE: " + lastLocationKey + "," + currentLocationKey);

        //functions
        updateMarker(lastLocationKey, currentLocationKey, grids);
        removeWallIfGoingOver(lastLocationKey, currentLocationKey, grids, lastLocation, currentLocation, gridWalls);

        //removeWallIfGoingOver();
        //checkIfNearbyWallsExists();
        //checkIfNearbyGridsHasAllWalls();

    }
    public static void updateMarker(String lastLocationKey, String currentLocationKey, HashMap<String, JPanel> grids) {
        JPanel oldTargetPanel = grids.get(lastLocationKey);
        oldTargetPanel.setBackground(Color.GRAY);
        JPanel targetPanel = grids.get(currentLocationKey);
        targetPanel.setBackground(Color.BLUE);
    }
    public static void removeWallIfGoingOver(String lastLocationKey, String currentLocationKey, HashMap<String, JPanel> grids, int[] lastLocation, int[] currentLocation, HashMap<String, JPanel> gridWalls) {
        System.out.println("Removing Walls");
        if (!Objects.equals(currentLocationKey, lastLocationKey)) {
            if (currentLocation[0] == lastLocation[0]) {
                JPanel targetGrid;
                JPanel targetPanel;
                if (currentLocation[1] > lastLocation[1]) {
                    targetGrid = grids.get(lastLocationKey);
                    targetPanel = gridWalls.get(lastLocationKey + "x");
                }
                else {
                    targetGrid = grids.get(currentLocationKey);
                    targetPanel = gridWalls.get(currentLocationKey + "x");

                }
                targetGrid.remove(targetPanel);
                targetGrid.invalidate();
                targetGrid.validate();

            } else if (currentLocation[1] == lastLocation[1]){
                JPanel targetGrid;
                JPanel targetPanel;
                if (currentLocation[0] > lastLocation[0]){
                    targetGrid = grids.get(lastLocationKey);
                    targetPanel = gridWalls.get(lastLocationKey + "y");
                }
                else{
                    targetGrid = grids.get(currentLocationKey);
                    targetPanel = gridWalls.get(currentLocationKey + "y");
                }
                targetGrid.remove(targetPanel);
                targetGrid.invalidate();
                targetGrid.validate();
            }
        }
    }
}

