import java.util.HashMap;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //Create a grid
        int xcord = 150;
        int ycord = 150;
        HashMap<String, JPanel> grids = new HashMap<>();
        HashMap<String, String> gridwalls = new HashMap<>();
        Scanner input = new Scanner(System.in);
        for (int i = 0; i<xcord; i++){
            for (int j = 0; j<ycord; j++){
                String xcordFormatted = String.format("%03d", i);
                String ycordFormatted = String.format("%03d", j);
                grids.put(xcordFormatted+","+ycordFormatted, null);
                gridwalls.put(xcordFormatted+","+ycordFormatted + ",y", "false");
                gridwalls.put(xcordFormatted+","+ycordFormatted + ",x", "false");
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
        System.out.println("gridsize:" + xcord +","+ ycord);
        gridPanel.setLayout(new GridLayout(xcord, ycord, 1, 1));  // 5x5 grid with 2px gaps

        mainFrame.add(gridPanel);
        mainFrame.setVisible(true);

        // Create the grids
        for (int i = 0; i < xcord; i++) {
            for (int j = 0; j < ycord; j++) {
                String key = String.format("%03d"+","+"%03d", i, j);

                // Create small panels to fill the grid
                JPanel smallGridPanel = new JPanel();
                smallGridPanel.setBackground(Color.GRAY);  // Set the background color to gray

                // Store the JPanel in the HashMap with the key
                grids.put(key, smallGridPanel);

                // Add the panel to the gridPanel (which has GridLayout)
                gridPanel.add(smallGridPanel);
            }
        }
        //check if all grids exist
        System.out.println(grids.size());

        System.out.println(grids);
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
                        moveNorth(currentLocation, grids);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveSouth(currentLocation, ycord, grids);
                        break;
                    case KeyEvent.VK_LEFT:
                        moveWest(currentLocation, grids);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveEast(currentLocation, xcord, grids);
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

    public static void moveEast(int[] currentLocation, int xcord ,HashMap<String, JPanel> grids) {
        System.out.println("E");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[1] < (xcord-1)) {
            currentLocation[1]++;
        }
        String xkey = String.format("%03d", currentLocation[0]);
        String ykey = String.format("%03d", currentLocation[1]);
        String currentLocationKey = xkey + "," + ykey;
        update(lastLocation,currentLocation, grids);



    }

    public static void moveWest(int[] currentLocation, HashMap<String, JPanel> grids){
        System.out.println("W");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[1] > 0){
            currentLocation[1]--;
        }
        String xkey = String.format("%03d", currentLocation[0]);
        String ykey = String.format("%03d", currentLocation[1]);
        String currentLocationKey = xkey + "," + ykey;
        update(lastLocation,currentLocation, grids);



    }

    public static void moveNorth(int[] currentLocation, HashMap<String, JPanel> grids){
        System.out.println("N");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[0] > 0){
            currentLocation[0]--;
        }
        String xkey = String.format("%03d", currentLocation[0]);
        String ykey = String.format("%03d", currentLocation[1]);
        String currentLocationKey = xkey + "," + ykey;
        update(lastLocation,currentLocation, grids);

    }

    public static void moveSouth(int[] currentLocation, int ycord, HashMap<String, JPanel> grids){
        System.out.println("S");
        int[] lastLocation = Arrays.copyOf(currentLocation, currentLocation.length);
        if (currentLocation[0] < (ycord-1)){
            currentLocation[0]++;
        }
        String xkey = String.format("%03d", currentLocation[0]);
        String ykey = String.format("%03d", currentLocation[1]);
        String currentLocationKey = xkey + "," + ykey;
        update(lastLocation,currentLocation, grids);

    }
    public static void update(int[] lastLocation, int[] currentLocation, HashMap<String, JPanel> grids) {
        // Initial variable creation
        String currentLocationKey = String.format("%03d", currentLocation[0]) + "," + String.format("%03d", currentLocation[1]);
        String lastLocationKey = String.format("%03d", lastLocation[0]) + "," + String.format("%03d", lastLocation[1]);
        System.out.println("IN UPDATE: " + lastLocationKey + "," + currentLocationKey);

        //functions
        updateMarker(lastLocationKey, currentLocationKey, grids);

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
}

