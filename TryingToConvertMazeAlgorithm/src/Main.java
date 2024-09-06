import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        //Create a grid
        int xcord = 5;
        int ycord = 5;
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
        gridPanel.setBounds(0, 0, 700, 700);
        gridPanel.setBackground(Color.WHITE);
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
        System.out.println(grids);
        JPanel targetPanel = grids.get("000,000");
        System.out.println(targetPanel);
        targetPanel.setBackground(Color.BLUE);

        int[] currentLocation = {0,0};

    }
    public void moveRight(String currentLocation){

    }
}