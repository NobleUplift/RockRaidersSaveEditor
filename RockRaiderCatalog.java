import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;
//import javax.json.JsonObject;
//import javax.json.JsonWriter;
import org.json.JSONObject;
import org.json.JSONArray;

class RockRaiderCatalog {
    static class RockRaider {
        public char[] name;
        public short level;
        public boolean driver;
        public boolean pilot;
        public boolean sailor;
        public boolean engineer;
        public boolean geologist;
        public boolean explosivesExpert;
        
        //public RockRaider() { }
        
        public short getAttributeScore() {
            short score = level;
            if (driver) score++;
            if (pilot) score++;
            if (sailor) score++;
            if (engineer) score++;
            if (geologist) score++;
            if (explosivesExpert) score++;
            return score;
        }
        
        public JSONObject toJSONObject() {
            JSONObject retval = new JSONObject();
            retval.put("name", new String(name).trim());
            retval.put("level", level);
            retval.put("driver", driver);
            retval.put("pilot", pilot);
            retval.put("sailor", sailor);
            retval.put("engineer", engineer);
            retval.put("geologist", geologist);
            retval.put("explosivesExpert", explosivesExpert);
            return retval;
        }
        
        public String toString() {
            return //"\t\"" + new String(name).trim() + "\": {" + 
                "\t{" + 
                "\r\n\t\t\"name\": \"" + new String(name).trim() + "\", " + 
                "\r\n\t\t\"level\": " + level + ", " + 
                "\r\n\t\t\"driver\": " + driver + ", " + 
                "\r\n\t\t\"pilot\": " + pilot + ", " + 
                "\r\n\t\t\"sailor\": " + sailor + ", " + 
                "\r\n\t\t\"engineer\": " + engineer + ", " + 
                "\r\n\t\t\"geologist\": " + geologist + ", " + 
                "\r\n\t\t\"explosivesExpert\": " + explosivesExpert + 
                "\r\n\t}";
        }
    }
    
    private static final String[] paths = {
        "C:\\Program Files\\LEGO Media\\Games\\Rock Raiders\\Data\\Saves\\", 
        "C:\\Program Files (x86)\\LEGO Media\\Games\\Rock Raiders\\Data\\Saves\\", 
        "D:\\Program Files\\LEGO Media\\Games\\Rock Raiders\\Data\\Saves\\",
        "D:\\Program Files (x86)\\LEGO Media\\Games\\Rock Raiders\\Data\\Saves\\",
        "."
    };
    
    public static void main(String[] args) {
        String directory = null;
        for (String path : paths) {
            if (Files.exists(Paths.get(path))) {
                directory = path;
                break;
            }
        }
        
        String file = null;
        Short saveID = null; 
        while (saveID == null) {
            String input = JOptionPane.showInputDialog(null, "Please specify the save file ID (0-5):");
            if (input == null) {
                return;
            }
            try {
                saveID = Short.parseShort(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, input + " is not a number.");
                saveID = null;
                continue;
            }
            
            if (saveID < 0 || 5 < saveID) {
                JOptionPane.showMessageDialog(null, "Number must be between 0-5.");
                saveID = null;
                continue;
            }
            
            if (Files.exists(Paths.get(directory + saveID + ".osf"))) {
                file = directory + saveID + ".osf";
            } else {
                JOptionPane.showMessageDialog(null, "Save file " + saveID + " does not contain a Rock Raider catalog.");
                saveID = null;
            }
        }
        
        Boolean odf = true;
        if (Files.exists(Paths.get(directory + saveID + ".json"))) {
            odf = null;
        }
        
        while (odf == null) {
            String input = JOptionPane.showInputDialog(null, "Would you like to load from the OSF or JSON file? (osf/json)");
            if (input == null) {
                return;
            }
            if ("osf".equals(input.toLowerCase())) {
                odf = true;
            }
            if ("json".equals(input.toLowerCase())) {
                odf = false;
            }
        }
        
        if (odf) {
            System.out.println("Parsing file: " + file);
            List<RockRaiderCatalog.RockRaider> raiders = getCatalog(file);
            if (raiders == null) {
                JOptionPane.showMessageDialog(null, "COuld not parse Rock Raider catalog for save file " + saveID + ".");
                return;
            }
            
            writeJSONFile(directory, saveID, raiders);
            JOptionPane.showMessageDialog(null, raiders.size() + " raiders processed.");
        } else {
            List<RockRaiderCatalog.RockRaider> raiders = readJSONFile(directory, saveID);
            if (raiders == null) {
                JOptionPane.showMessageDialog(null, "COuld not parse Rock Raider catalog for save file " + saveID + ".");
                return;
            }
            System.out.println(raiders);
            JOptionPane.showMessageDialog(null, raiders.size() + " raiders processed.");
        }
    }
    
    public static List<RockRaiderCatalog.RockRaider> readJSONFile(String directory, short saveID) {
        List<RockRaiderCatalog.RockRaider> retval = new ArrayList<RockRaiderCatalog.RockRaider>();
        String file = directory + saveID + ".json";
        
        String raw = null;
        try {
            raw = new Scanner(new File(file)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found " + directory + saveID + ".json.");
            return retval;
        }
        JSONArray a = new JSONArray(raw);
        
        for (Object o : a) {
            JSONObject obj = (JSONObject) o;
            RockRaiderCatalog.RockRaider raider = new RockRaiderCatalog.RockRaider();
            raider.level = (short) obj.getInt("level");
            char[] n = obj.getString("name").toCharArray();
            char[] name = new char[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
            for (short i = 0; i < name.length; i++) {
                name[i] = n[i];
            }
            raider.name = name;
            raider.driver = obj.getBoolean("driver");
            raider.pilot = obj.getBoolean("pilot");
            raider.sailor = obj.getBoolean("sailor");
            raider.engineer = obj.getBoolean("engineer");
            raider.geologist = obj.getBoolean("geologist");
            raider.explosivesExpert = obj.getBoolean("explosivesExpert");
            retval.add(raider);
        }
        
        return retval;
    }
    
    public static void writeJSONFile(String directory, short saveID, List<RockRaiderCatalog.RockRaider> raiders) {
        JSONArray json = new JSONArray();
        for (RockRaiderCatalog.RockRaider raider : raiders) {
            json.put(raider.toJSONObject());
        }
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(directory + saveID + ".json");
            writer.println(json);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static String getPrettyPrintJSON(List<RockRaiderCatalog.RockRaider> raiders) {
        String json = "";
        json += "[";
        short counter = 0;
        for (RockRaiderCatalog.RockRaider raider : raiders) {
            json += "\r\n" + raider.toString() + ",";
        }
        if (raiders.size() > 0) {
            json = json.substring(0, json.length() - 1);
        }
        json += "\r\n]";
        return json;
    }
    
    public static List<RockRaiderCatalog.RockRaider> getCatalog(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            
            byte[] header = new byte[8];
            inputStream.read(header);
            
            List<RockRaiderCatalog.RockRaider> raiders = new ArrayList<RockRaiderCatalog.RockRaider>();
            byte[] temp = new byte[20];
            
            int read = 0;
            while((read = inputStream.read(temp)) != -1) {
                RockRaiderCatalog.RockRaider raider = new RockRaiderCatalog.RockRaider();
                if (temp[4] == 0x01) { // SOH
                    raider.level = 1;
                } else if (temp[4] == 0x02) { // STX
                    raider.level = 2;
                } else if (temp[4] == 0x03) { // ETX
                    raider.level = 3;
                } else {
                    raider.level = 0;
                }
                
                char[] name = new char[11];
                for (int i = 0; i < 11; i++) {
                    name[i] = (char) temp[i + 8];
                }
                raider.name = name;
                
                if ((temp[0] & 0x01) == 1) {
                    raider.pilot = true;
                }
                
                if ((temp[0] & 0x02) == 2) {
                    raider.sailor = true;
                }
                
                if ((temp[0] & 0x04) == 4) {
                    raider.driver = true;
                }
                
                if ((temp[0] & 0x08) == 8) {
                    raider.explosivesExpert = true;
                }
                
                if ((temp[0] & 0x10) == 16) {
                    raider.engineer = true;
                }
                
                if ((temp[0] & 0x20) == 32) {
                    raider.geologist = true;
                }
                
                raiders.add(raider);
            }
            
            inputStream.close();  
            
            return raiders;
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");    
        } catch(IOException ex) {
            System.out.println("Error reading file '"  + fileName + "'");     
        }
        return null;
    }
}