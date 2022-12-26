package net.shortninja.staffplus.bungee.player;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.shortninja.staffplus.bungee.StaffPlus;

import java.io.File;
import java.io.IOException;

public class Save {

    private final StaffPlus staffPlus;
    private File usersFile;
    private Configuration users;

    public Save(StaffPlus staffPlus){
        this.staffPlus = staffPlus;
        usersFile = new File(staffPlus.getDataFolder(),"data.yml");
        createFile();
    }

    public void createFile(){
        try{
            if(!usersFile.exists()){
                usersFile.createNewFile();
                users = ConfigurationProvider.getProvider(YamlConfiguration.class).load(usersFile);
            }
        }catch (IOException e ){
            e.printStackTrace();
            staffPlus.getLogger().info("SUMTING WONG");
        }
    }

    public void save(User user){

    }


}
