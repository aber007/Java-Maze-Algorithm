import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Create a grid
        int xcord = 5;
        int ycord = 5;
        HashMap<String, String> grids = new HashMap<>();
        HashMap<String, String> gridwalls = new HashMap<>();
        Scanner input = new Scanner(System.in);
        for (int i = 0; i<xcord; i++){
            for (int j = 0; j<ycord; j++){
                String xcordFormatted = String.format("%03d", i);
                String ycordFormatted = String.format("%03d", j);
                grids.put(xcordFormatted+","+ycordFormatted, "grid");
                gridwalls.put(xcordFormatted+","+ycordFormatted + ",y", "ywall");
                gridwalls.put(xcordFormatted+","+ycordFormatted + ",x", "xwall");
            }
        }

        System.out.println(grids);
        System.out.println("Choose a grid");
        String grid = input.nextLine();
        System.out.println(grids.get(grid));
        System.out.println(gridwalls.get(grid+",y"));
        System.out.println(gridwalls.get(grid+",x"));

    }
}