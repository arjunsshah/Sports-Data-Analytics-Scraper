import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class MLBStats {

    private URL url;
    private HttpURLConnection httpConnection;
    private String html;
    private String position;
    private Map<String, String> stats;
    private boolean validPlayer;
    
    //constructor
    public MLBStats(String player) {
        this.playerURL(player);
        this.html = getContentsString().toLowerCase();
        this.position = patternMatch("</h1> <p>    <strong>position:</strong>    (.*?)  </p><p>", 1);
        this.collectStats();
    }
    
    public static boolean validPlayer(String player) {
        MLBStats test = new MLBStats(player);
        return test.validPlayer;
    }
    
    //finds unique playerID with first 5 letters of last name and first 2 letters of first name for html
    private void playerURL(String player) {
        int space = player.indexOf(' ');
        String playerID = player.substring(space + 1, Math.min(space + 6, player.length()));
        playerID += player.substring(0, Math.min(2, space)); 
        this.initConnection("https://www.baseball-reference.com/players/" + player.charAt(space + 1) + 
                "/" + playerID + "01.shtml");
    }
    
    //establish connection to url
    public void initConnection(String url) {
        try {
            this.url = new URL(url);
            URLConnection connection = this.url.openConnection();
            this.httpConnection = (HttpURLConnection) connection;
            this.validPlayer = true;

        } catch (Exception e) {
            this.validPlayer = false;
        }
    }
    
  //get and store the html as a single line string
    public String getContentsString() {
        String contents = "";
        try {
            Scanner in = new Scanner(httpConnection.getInputStream());
            while (in.hasNextLine()) {
                String line = in.nextLine();
                contents = contents + line;
            }
            in.close();
        } catch (IOException e) {
        }
        return contents;
    }
    
    //pattern matches the url with the regex input and returns the respective matcher group
    private String patternMatch(String regex, int groupNum) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.html);
        if(matcher.find()) {
            return matcher.group(groupNum);
        }
        return null;
    }
    
    //uses regex to get all stats
    private void collectStats() {
        this.stats = new HashMap<String, String>();
        stats.put("Position", position);
        stats.put("Wins Above Replacement", patternMatch("<strong>war</strong></span><p>(.*?)</p></div>", 1));
        if (position.equals("pitcher")) {
            stats.put("Wins", patternMatch("<div><span class=\"poptip\" data-tip=\"wins\"><strong>w</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Losses", patternMatch("<div><span class=\"poptip\" data-tip=\"losses\"><strong>l</strong></span><p>(.*?)</p></div>", 1));
            stats.put("ERA", patternMatch("<strong>era</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Games", patternMatch("<strong>g</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Games Started", patternMatch("<strong>gs</strong></span><p>(.*?)</p></div>", 1));
        } else {
            stats.put("At Bats", patternMatch("<div><span class=\"poptip\" data-tip=\"at bats\"><strong>ab</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Hits", patternMatch("<strong>h</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Home Runs", patternMatch("<strong>hr</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Batting Average", patternMatch("<strong>ba</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Runs", patternMatch("<strong>r</strong></span><p>(.*?)</p></div>", 1));
            stats.put("RBIs", patternMatch("<strong>rbi</strong></span><p>(.*?)</p></div>", 1));
            stats.put("Stolen Bases", patternMatch("<strong>sb</strong></span><p>(.*?)</p></div>", 1));
        }
    }
    
    //populates the map of statistic type to a player's career stats
    public Map<String, String> getStats(){
        return this.stats;
    }
    
    public static void main(String[] args) {
        MLBStats mookie = new MLBStats("jacob degrom");
        for(Map.Entry<String, String> entry: mookie.getStats().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    
}
