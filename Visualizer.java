import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
public class Visualizer extends JPanel {

    //TODO
    private final static BufferedImage mlb = image("mlb.png");
    private final static BufferedImage nfl = image("nfl.png");
    private final static BufferedImage nba = image("nba.jpg");
    
    public Visualizer(String sport, String player, String year) {
        Map<String, String> playerStats = null;
        switch(sport){
        case "baseball": 
            MLBStats playerScrap = new MLBStats(player);
            playerStats = playerScrap.getStats();
            break;
        case "football":
            NFLParser nflParser = new NFLParser();
            playerStats = nflParser.getPlayer(player);
            
            break;
        case "basketball":
            NBAParser statparse = new NBAParser();
            HashMap<String, Map<String, String>> here = new HashMap<String, Map<String, String>>();
            here = statparse.PlayerStats(player);
            playerStats = here.get(year);
            break;
        default:
            break;
        }
        for(Map.Entry<String, String> entry: playerStats.entrySet()) {
            final JLabel statline = new JLabel(entry.getKey() + ": " + entry.getValue());
            this.add(statline, BorderLayout.NORTH);
        }
    }
    
    private static BufferedImage image(String imageFile) {
        try {
            return ImageIO.read(new File(imageFile));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static boolean validSport(String sport) {
        return sport.equals("baseball") || sport.equals("football") || sport.equals("basketball");
    }
    
    public static void main(String[] args) {
        System.out.println("What sport?");
        Scanner input = new Scanner(System.in);
        String sport = input.nextLine().toLowerCase();
        while (!Visualizer.validSport(sport)) {
            System.out.println("Invalid sport! Please choose from baseball, football, or basketball");
            sport = input.nextLine().toLowerCase();
        }
        System.out.println("What player?");
        System.out.println("Note: Basketball and Football players are case sensitive"); //added here
        String player = "";
        String year = "";
        switch(sport){
        case "baseball": 
            player = input.nextLine().toLowerCase();
            while (!MLBStats.validPlayer(player)) {
                System.out.println("Invalid player! ");
                sport = input.nextLine().toLowerCase();
            }
            break;
        case "football":
            player = input.nextLine();
            break;
        case "basketball":
            player = input.nextLine();
            while (!NBAParser.validPlayer(player)) {
                System.out.println("Invalid player! Input again! ");
                player = input.nextLine().toLowerCase();
            }
            NBAParser exampleparse = new NBAParser();
            exampleparse.PlayerStats(player);
            ArrayList<String> years = exampleparse.getYears();
            for (int i = 0; i < years.size(); i++) {
                System.out.println(years.get(i));       
            }

            System.out.println("Now, enter a year to lookup for this player!");
            year = input.nextLine();
            while (!NBAParser.validYear(year, player)) {
                System.out.println("Invalid year! Input again! ");
                year = input.nextLine().toLowerCase();
            }
            break;
        default:
            break;
        }
        input.close();
        Visualizer jpanel = new Visualizer(sport, player, year);
        JFrame statBoard = new JFrame("Professional Player Stats");
        statBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statBoard.pack();
        statBoard.setVisible(true);
        statBoard.add(jpanel);

    }
}