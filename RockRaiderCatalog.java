import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

class RockRaiderCatalog {
    class RockRaider {
        public short level;
        public char[] name;
        public boolean driver;
        public boolean pilot;
        public boolean sailor;
        public boolean engineer;
        public boolean geologist;
        public boolean explosivesExpert;
        
        public RockRaider() { }
        
        public String toString() {
			return "\"" + new String(name) + "\"" + 
			"\r\n\tLevel " + level + 
			"\r\n\tDriver: " + driver + 
			"\r\n\tPilot: " + pilot + 
			"\r\n\tSailor: " + sailor + 
			"\r\n\tEngineer: " + engineer + 
			"\r\n\tGeologist: " + geologist + 
			"\r\n\tExplosive Expert: " + explosivesExpert + 
			"\r\n";
        }
    }
    
    public static void main(String[] args) {
        String file = "C:\\Program Files\\LEGO Media\\Games\\Rock Raiders\\Data\\Saves\\0.osf";
        
        RockRaiderCatalog rrc = new RockRaiderCatalog();
        List<RockRaiderCatalog.RockRaider> raiders = rrc.getCatalog(file);
		
        for (RockRaiderCatalog.RockRaider raider : raiders) {
			System.out.println(raider.toString());
        }
    }
    
    public List<RockRaiderCatalog.RockRaider> getCatalog(String fileName) {
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
            System.out.println(raiders.size() + " raiders processed.");

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