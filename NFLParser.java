import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NFLParser {

    private String baseURL;
    private Document currentDoc;
    private HashMap<String, String> qbHashMap = new HashMap<String, String>();
    private HashMap<String, String> oLineHashMap = new HashMap<String, String>();
    private HashMap<String, String> defenseHashMap = new HashMap<String, String>();
    private HashMap<String, String> specialHashMap = new HashMap<String, String>();
    private HashSet<String> oLineSet = new HashSet<String>();
    private HashSet<String> backSet = new HashSet<String>();
    private HashSet<String> defenseSet = new HashSet<String>();
    private HashSet<String> specialSet = new HashSet<String>();

    public NFLParser() {
        this.baseURL = "https://www.pro-football-reference.com/players/A/";

        try {
            this.currentDoc = Jsoup.connect(this.baseURL).get();
        } catch (IOException e) {
            System.out.println("Could not get NFL statistics data");
        }
    }
    
    // Create HashMap<String, String> for user input and stat
    // DEFENSE (DT)
    // Interceptions
    // Interception Yards
    // Interception Touchdowns
    // Interception Long
    
    // Pass Defended
    
    // Fumbles Forced
    // Fumbles Total
    // Fumbles Yards
    // Fumbles Touchdowns
    
    // Sacks
    
    // Tackles Combined
    
    //QB
    // Passes Completed
    // Completion Percentage
    // Pass Yards
    // Touchdowns
    // Interceptions
    
    // Return position of a player
    // Run inside getPlayer
    public String getPosition() {
        // get class
        Elements element = this.currentDoc.getElementsByClass("players");
        Elements pTag = element.select("p");
        Element a = pTag.get(1);
        String str = a.html();
        String position = "";
       
        
        Pattern pattern = Pattern.compile("(</strong>:)([\\s\\w]+)");
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            position = matcher.group(2).replaceAll("\\s", "");
        }

        return position;
    }

    
    public void qbHashMap() {
        qbHashMap.put("passes completed", "pass_cmp");
        qbHashMap.put("completion percentage", "pass_cmp_perc");
        qbHashMap.put("pass yards", "pass_yds");
        qbHashMap.put("touchdowns", "pass_td");
        qbHashMap.put("interceptions", "pass_int");
    }
    
    public void oLineHashMap() {
        oLineHashMap.put("defense interceptions", "def_int");
        oLineHashMap.put("defense interceptions yards", "def_int_yds");
        oLineHashMap.put("fumbles forced", "fumbles_forced");
        oLineHashMap.put("fumbles", "fumbles");
    }
    
    public void defenseHashMap() {
        defenseHashMap.put("defense interceptions", "def_int");
        defenseHashMap.put("defense interceptions yards", "def_int_yds");
        defenseHashMap.put("fumbles forced", "fumbles_forced");
        defenseHashMap.put("fumbles", "fumbles");
        defenseHashMap.put("total tackles", "tackles_combined");
    }
    
    public void specialHashMap() {
        specialHashMap.put("Field goals attempted", "fga");
        specialHashMap.put("field goals made", "fgm");
    }
    
    // 1) Oline
    // 2) Backs/Receivers
    // 3) Defense
    // 4) Special Teams
    public void createPositionMaps() {
        // 1)
        oLineSet.add("C");
        oLineSet.add("OG");
        oLineSet.add("OT");
        // 2) 
        backSet.add("QB");
        backSet.add("FB");
        backSet.add("WR");
        backSet.add("TE");
        // 3)
        defenseSet.add("DT");
        defenseSet.add("DE");
        defenseSet.add("MLB");
        defenseSet.add("OLB");
        defenseSet.add("CB");
        defenseSet.add("S");
        defenseSet.add("NT");
        // 4)
        specialSet.add("K");
        specialSet.add("KOS");
        specialSet.add("P");
    }
    
    public Map<String, String> getPlayer(String name) {
        String begUrl = "https://www.pro-football-reference.com";
        // 1) First start at players by alphabet page

        // 2) Get first letter of players last name
        String[] nameArr = name.split("\\s");
        char c = Character.toUpperCase(nameArr[1].charAt(0));
        // 3) Select that letter and move to that href page
        try {
            this.currentDoc = Jsoup.connect(begUrl + "//players//" + c).get();
        }           
        catch (IOException e) {
            System.out.println("Couldn't get letter");
        }
        // 4) Then do a search of the name and move to the href page
        Elements el = this.currentDoc.getElementsByClass("section_content");
        Elements pTag = el.select("p");
        Element a = pTag.get(2);        

        String playerHref = "";
        for (Element p : pTag) {
            Pattern pattern = Pattern.compile(name);
            Matcher matcher = pattern.matcher(p.html());
            if (matcher.find()) {
                playerHref = p.html();
                break;
            }
        }
        
        Pattern pattern = Pattern.compile("(href=\")([\\w\\s\\d.//]+)");
        Matcher matcher = pattern.matcher(playerHref);
        if (matcher.find()) {

            try {
                this.currentDoc = Jsoup.connect(begUrl + matcher.group(2)).get();
            }           
            catch (IOException e) {
                System.out.println("Couldn't get player");
            }
        }
        return this.getStat();
    }
    
    

    
    
    public HashMap<String, String> getStat() {
         Element links = this.currentDoc.select("tfoot").get(0);
         // Use regex or figure out how to use JSoup to get specific stats
         qbHashMap();
         oLineHashMap();
         defenseHashMap();
         specialHashMap();
         String s = links.html();
         // use regex to get match stat with data point
         
         String position = this.getPosition();
         HashMap<String, String> statHashMap = new HashMap<String, String>();
         
         this.createPositionMaps();
         // Implement check for player position
         // in each iterate through all stats for that category and add to hashMap<String, String>
         // where stat names are mapped to the value of the stat (as string)
         if (oLineSet.contains(position)) {
             for (Map.Entry<String, String> i : oLineHashMap.entrySet()) {
                 String statPattern = "(\"" + (String) i.getValue() + "\">)([\\d]+)";
                 Pattern pattern = Pattern.compile(statPattern);
                 Matcher matcher = pattern.matcher(s);
                          
                 if (matcher.find()) {
                     System.out.println(matcher.group(2));
                     statHashMap.put((String) i.getKey(), matcher.group(2));
                 } else {
                     return null;
                 }
             }
             
         } else if (backSet.contains(position)) {
             for (Map.Entry<String, String> i : qbHashMap.entrySet()) {
                 String statPattern = "(\"" + (String) i.getValue() + "\">)([\\d]+)";
                 Pattern pattern = Pattern.compile(statPattern);
                 Matcher matcher = pattern.matcher(s);
                          
                 if (matcher.find()) {
                     statHashMap.put((String) i.getKey(), matcher.group(2));
                 } else {
                     return null;
                 }
             }
         } else if (defenseSet.contains(position)) {
             for (Map.Entry<String, String> i : defenseHashMap.entrySet()) {
                 String statPattern = "(\"" + (String) i.getValue() + "\">)([\\d]+)";
                 Pattern pattern = Pattern.compile(statPattern);
                 Matcher matcher = pattern.matcher(s);
                          
                 if (matcher.find()) {
                     statHashMap.put((String) i.getKey(), matcher.group(2));
                 } else {
                     return null;
                 }
             }
         } else {
             for (Map.Entry<String, String> i : specialHashMap.entrySet()) {
                 String statPattern = "(\"" + (String) i.getValue() + "\">)([\\d]+)";
                 Pattern pattern = Pattern.compile(statPattern);
                 Matcher matcher = pattern.matcher(s);
                          
                 if (matcher.find()) {
                     statHashMap.put((String) i.getKey(), matcher.group(2));
                 } else {
                     return null;
                 }
             }
         }
         return statHashMap;
    }

}