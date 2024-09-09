import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        //Create a grid
        int xcord = 10; // Changed to smaller number for testing
        int ycord = 10; // Changed to smaller number for testing
        HashMap<String, JPanel> grids = new HashMap<>();
        HashMap<String, String> gridwalls = new HashMap<>();

        Scanner input = new Scanner(System.in);
        for (int i = 0; i < xcord; i++) {
            for (int j = 0; j < ycord; j++) {
                String xcordFormatted = String.format("%03d", i);
                String ycordFormatted = String.format("%03d", j);
                grids.put(xcordFormatted + "," + ycordFormatted, null);
                gridwalls.put(xcordFormatted + "," + ycordFormatted + ",y", "false");
                gridwalls.put(xcordFormatted + "," + ycordFormatted + ",x", "false");
            }
        }

        //create main window
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Maze Algorithm");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 900);

        mainFrame.setLayout(null);

        // Create gridPanel with GridLayout to automatically manage the grid
        JPanel gridPanel = new JPanel();
        gridPanel.setBounds(0, 0, 780, 780);
        gridPanel.setBackground(Color.WHITE);

        // Adjust the grid size to account for wall gaps
        gridPanel.setLayout(new GridLayout(xcord * 2 - 1, ycord * 2 - 1, 1, 1));  // Adjusted for walls

        mainFrame.add(gridPanel);
        mainFrame.setVisible(true);

        // Create the grids and walls
        for (int i = 0; i < xcord * 2 - 1; i++) {
            for (int j = 0; j < ycord * 2 - 1; j++) {
                JPanel panel = new JPanel();

                if (i % 2 == 0 && j % 2 == 0) {
                    // This is a grid cell
                    panel.setBackground(Color.GRAY);
                    String key = String.format("%03d,%03d", i / 2, j / 2);
                    grids.put(key, panel);
                } else if (i % 2 == 0) {
                    // This is a horizontal wall (x-wall)
                    panel.setBackground(Color.RED);  // Represent walls with red color
                    String key = String.format("%03d,%03d,x", i / 2, j / 2);
                    gridwalls.put(key, "true");
                } else if (j % 2 == 0) {
                    // This is a vertical wall (y-wall)
                    panel.setBackground(Color.BLUE);  // Represent walls with blue color
                    String key = String.format("%03d,%03d,y", i / 2, j / 2);
                    gridwalls.put(key, "true");
                } else {
                    // This is a corner/empty panel (intersection of walls)
                    panel.setBackground(Color.BLACK);  // Just a filler for corners
                }

                gridPanel.add(panel);
            }
        }
    }
}
