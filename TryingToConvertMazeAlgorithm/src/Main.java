import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Create a grid
        int xcord = 5;
        int ycord = 5;
        HashMap<String, String> grids = new HashMap<>();
        Scanner input = new Scanner(System.in);
        for (int i = 0; i<xcord; i++){
            String xcordFormatted = String.format("%03d", i);
            for (int j = 0; j<ycord; j++){
                String ycordFormatted = String.format("%03d", j);
                grids.put(xcordFormatted+","+ycordFormatted, null);

            }
        }
        System.out.println(grids);
        System.out.println("Choose a grid");
        String grid = input.nextLine();
        System.out.println(grids.get(grid));
    }
}