
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class NBAParser {
    private String baseURL;
    private String homeURL; 
    private Document currentdoc;
    private boolean found;
    private ArrayList<String> years;
    
   
    
    public NBAParser() {
        this.baseURL = "https://www.basketball-reference.com/players/";
        this.homeURL = "https://www.basketball-reference.com";
        this.found = false;
        this.years = new ArrayList<String>();
  
        
        try {
            this.currentdoc = Jsoup.connect(this.baseURL).get();
        } catch (IOException e) {
            System.out.println("error");
        }
    }
    
    public String getLink(String input){
        for (Element a: currentdoc.getElementsByTag("a ")){
            if (a.text().equals(input)){
                System.out.print(a.attr("href"));
                return a.attr("href");
            }
        }   
        return "";
    }
    
    public HashMap<String, Map<String, String>> PlayerStats (String player) {
        HashMap<String, Map<String, String>> yeartostats = new HashMap();
        Map<String, String> stats = new HashMap();
        Document playerslist = null;
        Document playerpage = null;
        String[] last = player.split(" ");
        String lastname = last[last.length-1].toLowerCase();
        Character lastnamechar = lastname.charAt(0);
        String playerurl = this.baseURL + lastnamechar + "/";
        //System.out.println(playerurl);
        
        
        try {
            playerslist = Jsoup.connect(playerurl).get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Element table = playerslist.select("table").get(0);
        Elements rows = table.select("tr");
        Boolean found = false;
       
        
        

        
        
        for (int i = 1  ; i < rows.size(); i++) {
            Element row = rows.get(i);
    
            Element key = row.selectFirst("a ");
            
            if (key.text().equals(player)) {
                homeURL += key.attr("href");
                this.found = true;
            }
            
        } 
        if (this.found == false) {
            System.out.println("Player not found!");
            return yeartostats;
        }
        
        
        try {
            playerpage = Jsoup.connect(homeURL).get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        table = playerpage.select("tbody").get(0);
        rows = table.select("tr");
        
        for (int i = 0; i < rows.size(); i++) { 
            Element row = rows.get(i);
            
    
            Element x = row.selectFirst("th ");
            Element year = row.selectFirst("a ");

            try {
                stats.put(x.attr("data-stat"), year.text());
                Elements selectstats = row.select("td");
                for (Element e: selectstats) {
                    if (e.selectFirst("a ") == null) {
                        stats.put(e.attr("data-stat"), e.text());
                    } else  {
                        Element something = e.selectFirst("a ");
                        stats.put(e.attr("data-stat"), something.text());
                    }
                }
                String text = year.text();
                text = text.split("-")[0];
                yeartostats.put(text, stats);
                years.add(text);
            } catch (Exception e) {
                ;
            }
            
         
        } 
        
        return yeartostats;
        
    }
    
    public Map<String, String> getStats (HashMap<String, Map<String, String>> yeartostats, String year) {
        Map<String, String> stats = yeartostats.get(year);

        return stats;
    }
    
    public static boolean validPlayer(String player) {
        NBAParser test = new NBAParser();
        test.PlayerStats(player);
        return test.found;
    }
    
    public static boolean validYear(String year, String player) {
        NBAParser test = new NBAParser();
        test.PlayerStats(player);
        if (test.years.contains(year)) {
            return true;
        } return false;
    }
    
    public ArrayList<String> getYears() {
        return this.years;
    }
    
}