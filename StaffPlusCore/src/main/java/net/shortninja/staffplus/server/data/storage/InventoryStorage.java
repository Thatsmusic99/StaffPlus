package net.shortninja.staffplus.server.data.storage;

import net.shortninja.staffplus.StaffPlus;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class InventoryStorage {

    private File file;
    private YamlConfiguration inventories;
    private final StaffPlus staffplus;

    public InventoryStorage(StaffPlus staffPlus){
        this.staffplus = staffPlus;
        file = createFile();
    }

    public File createFile(){
        file = new File(staffplus.getDataFolder()+"inventories.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
                inventories = YamlConfiguration.loadConfiguration(file);
                inventories.contains("inventories");
                inventories.save(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            inventories = YamlConfiguration.loadConfiguration(file);
        }
        return file;
    }
}
