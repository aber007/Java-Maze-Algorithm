import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import javax.swing.*;

public class Main_tests {
    private static final boolean debug = false;
    public static String[] possibleDirections = new String[4];
    public static boolean algorithmStatus = false;
    public static ArrayList<String> backtrackLocation = new ArrayList<>();
    public static boolean backTrackStatus = false;
    public static void main(String[] args) {
        //Create a grid
        int xCord = 150;
        int yCord = 150;
        HashMap<String, JPanel> grids = new HashMap<>();
        HashMap<String, JPanel> gridWalls = new HashMap<>();
        HashMap<String, String> gridHasAllWalls = new HashMap<>();
        for (int i = 0; i<xCord; i++){
            for (int j = 0; j<yCord; j++){
                String xCordFormatted = String.format("%03d", i);
                String yCordFormatted = String.format("%03d", j);
                grids.put(xCordFormatted+","+yCordFormatted, null);
                gridHasAllWalls.put(xCordFormatted+","+yCordFormatted, "TRUE");
                gridWalls.put(xCordFormatted+","+yCordFormatted + "y", null);
                gridWalls.put(xCordFormatted+","+yCordFormatted + "x", null);
            }
        }
        //create main window
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Maze Algorithm");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        gridHasAllWalls.put("000,000", "FALSE");
        mainFrame.setLayout(null);

        // Create gridPanel with GridLayout to automatically manage the grid
        JPanel gridPanel = new JPanel();
        gridPanel.setBounds(0, 0, 780, 780);
        gridPanel.setBackground(Color.WHITE);
        System.out.println("gridSize:" + xCord +","+ yCord);
        gridPanel.setLayout(new GridLayout(xCord, yCord, 0, 0));  // 5x5 grid with 2px gaps

        //Create buttons
        JButton startButton = new JButton("Start");
        startButton.setBounds(850, 50, 100, 50);
        startButton.setBackground(Color.GREEN);

        // Define the toggle minimize action


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
                    int wallWidth = (int) Math.round(horizontalGridSize * 0.2);
                    gridWallVertical.setBounds(horizontalGridSize - wallWidth, 0, wallWidth, horizontalGridSize);
                    gridWallVertical.setBackground(Color.BLACK);
                    gridWalls.put((key + "x"), gridWallVertical);
                    smallGridPanel.add(gridWallVertical);
                }
                if (!keySplit[0].equals((String.format("%03d", yCord - 1)))) {
                    JPanel gridWallHorizontal = new JPanel();
                    int wallHeight = (int) Math.round(horizontalGridSize * 0.2);
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
        startButton.addActionListener(_ -> startAlgorithm(currentLocation, grids, gridWalls, gridHasAllWalls, yCord, xCord, startButton));
        mainFrame.add(startButton);
        mainFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        moveNorth(currentLocation, grids, gridWalls, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveSouth(currentLocation, yCord, grids, gridWalls, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_LEFT:
                        moveWest(currentLocation, grids, gridWalls, gridHasAllWalls);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveEast(currentLocation, xCord, grids, gridWalls, gridHasAllWalls);
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

    public static void moveEast(int[] currentLocation, int xCord ,HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls) {
        System.out.println("E");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[1] < (xCord-1)) {
            currentLocation[1]++;
        }
        update(lastLocation,currentLocation, grids, gridWalls, gridHasAllWalls);



    }

    public static void moveWest(int[] currentLocation, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls) {
        System.out.println("W");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[1] > 0){
            currentLocation[1]--;
        }
        update(lastLocation,currentLocation, grids, gridWalls, gridHasAllWalls);



    }

    public static void moveNorth(int[] currentLocation, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls) {
        System.out.println("N");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[0] > 0){
            currentLocation[0]--;
        }
        update(lastLocation,currentLocation, grids, gridWalls, gridHasAllWalls);

    }

    public static void moveSouth(int[] currentLocation, int yCord, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls) {
        System.out.println("S");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[0] < (yCord-1)){
            currentLocation[0]++;
        }
        update(lastLocation,currentLocation, grids, gridWalls, gridHasAllWalls);

    }
    public static void update(int[] lastLocation, int[] currentLocation, HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls) {
        // Initial variable creation
        String currentLocationKey = String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1]);
        String lastLocationKey = String.format("%03d", lastLocation[0]) + "," + String.format("%03d", lastLocation[1]);
        System.out.println("IN UPDATE: " + lastLocationKey + "," + currentLocationKey);

        //functions
        updateMarker(lastLocationKey, currentLocationKey, grids);
        removeWallIfGoingOver(lastLocationKey, currentLocationKey, grids, lastLocation, currentLocation, gridWalls, gridHasAllWalls);
        checkPossibleDirections(currentLocation, gridHasAllWalls);

        if (debug){
            for (String key : grids.keySet()) {
                JPanel targetPanel = grids.get(key);
                if (Objects.equals(gridHasAllWalls.get(key), "FALSE")) {
                    targetPanel.setBackground(Color.RED);
                } else {
                    targetPanel.setBackground(Color.GREEN);
                }
            }
        }
        //checkIfNearbyWallsExists();
        //checkIfNearbyGridsHasAllWalls();

    }
    public static void checkPossibleDirections(int[] currentLocation, HashMap<String, String> gridHasAllWalls) {
        System.out.println("CHECK POSSIBLE DIRECTIONS");
        System.out.println(Arrays.toString(currentLocation));

        Arrays.fill(possibleDirections, null);

        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0] - 1) + "," + String.format("%03d", currentLocation[1])).equals("TRUE")) {
                System.out.println("POSSIBLE NORTH");
                possibleDirections[0] = "NORTH";
            }
        } catch (Exception _){}
        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0] + 1) + "," + String.format("%03d", currentLocation[1])).equals("TRUE")) {
                System.out.println("POSSIBLE SOUTH");
                possibleDirections[1] = "SOUTH";
            }
        } catch (Exception _){}
        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1] + 1)).equals("TRUE")) {
                System.out.println("POSSIBLE EAST");
                possibleDirections[2] = "EAST";
            }
        } catch (Exception _){}
        try {
            if (gridHasAllWalls.get(String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1] - 1)).equals("TRUE")) {
                System.out.println("POSSIBLE WEST");
                possibleDirections[3] = "WEST";
            }
        } catch (Exception _){}
        System.out.println(Arrays.toString(possibleDirections));

    }
    public static void updateMarker(String lastLocationKey, String currentLocationKey, HashMap<String, JPanel> grids) {
        JPanel oldTargetPanel = grids.get(lastLocationKey);
        oldTargetPanel.setBackground(Color.GRAY);
        JPanel targetPanel = grids.get(currentLocationKey);
        targetPanel.setBackground(Color.BLUE);
    }
    public static void removeWallIfGoingOver(String lastLocationKey, String currentLocationKey, HashMap<String, JPanel> grids, int[] lastLocation, int[] currentLocation, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls) {
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
                gridHasAllWalls.put(lastLocationKey, "FALSE");
                gridHasAllWalls.put(currentLocationKey, "FALSE");

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
                gridHasAllWalls.put(lastLocationKey, "FALSE");
                gridHasAllWalls.put(currentLocationKey, "FALSE");
                System.out.println("END OF REMOVING WALLS");
            }
        }
    }
    public static void startAlgorithm(int[] currentLocation ,HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls, int yCord, int xCord, JButton button) {
        algorithmStatus = !algorithmStatus;
        System.out.println(algorithmStatus);
        if (algorithmStatus){
            button.setBackground(Color.RED);
            button.setText("Stop");
        }
        else{
            button.setBackground(Color.GREEN);
            button.setText("Start");
        }
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        update(lastLocation, currentLocation, grids, gridWalls, gridHasAllWalls);
        // Start the algorithm in a separate thread
        Thread algorithmThread = new Thread(() -> algorithm(currentLocation, grids, gridWalls, gridHasAllWalls, yCord, xCord, lastLocation));

        algorithmThread.start(); // Start the thread
    }
    public static void algorithm(int[] currentLocation ,HashMap<String, JPanel> grids, HashMap<String, JPanel> gridWalls, HashMap<String, String> gridHasAllWalls, int yCord, int xCord, int[] lastLocation) {
        while (algorithmStatus) {
            String key = String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1]);
            if (!backTrackStatus){
            backtrackLocation.add(key);
            }
            ArrayList<String> possibleDirectionsList = new ArrayList<>();
            for (String direction : possibleDirections) {
                if (direction != null) {
                    possibleDirectionsList.add(direction);
                }
            }
            try{
                Random rand = new Random();
                int randIndex = rand.nextInt(possibleDirectionsList.size());
                String directionToMove = possibleDirectionsList.get(randIndex);
                backTrackStatus = false;
                switch (directionToMove) {
                    case "NORTH":
                        moveNorth(currentLocation, grids, gridWalls, gridHasAllWalls);
                        break;
                    case "SOUTH":
                        moveSouth(currentLocation, yCord, grids, gridWalls, gridHasAllWalls);
                        break;
                    case "WEST":
                        moveWest(currentLocation, grids, gridWalls, gridHasAllWalls);
                        break;
                    case "EAST":
                        moveEast(currentLocation, xCord, grids, gridWalls, gridHasAllWalls);
                        break;

                }
            } catch (Exception e){
                backTrackStatus = true;
                System.out.println("Start Backtrack");
                System.out.println(backtrackLocation);
                backtrackLocation.removeLast();

                String currentLocationKey = String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1]);
                JPanel targetPanel = grids.get(currentLocationKey);
                System.out.println(targetPanel);
                targetPanel.setBackground(Color.GRAY);

                int backTrackX = Integer.parseInt(backtrackLocation.getLast().split(",")[0]);
                int backTrackY = Integer.parseInt(backtrackLocation.getLast().split(",")[1]);
                currentLocation = new int[]{backTrackX, backTrackY};

                update(lastLocation, currentLocation, grids, gridWalls, gridHasAllWalls);
            }
        }
    }
}

