package net.shortninja.staffplus.bungee.data.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.shortninja.staffplus.bungee.StaffPlus;
import net.shortninja.staffplus.bungee.util.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Options {

    private static final int CURRENT_VERSION = 6207;
    private StaffPlus staffPlus;
    private File configFile;
    private Configuration config;

    public Options(StaffPlus staffPlus){
        this.staffPlus = staffPlus;
        configFile = new File(staffPlus.getDataFolder(), "config.yml");
    }

    public Configuration setupConfig(){
        try {
            if (!configFile.exists()) {
                InputStream is = staffPlus.getResourceAsStream("proxyConfig.yml");
                configFile.createNewFile();
                OutputStream os = new FileOutputStream(configFile);
                ByteStreams.copy(is, os);
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(staffPlus.getDataFolder(),"config.yml"));
            }else{
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(staffPlus.getDataFolder(),"config.yml"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create configuration file", e);
        }
        //update();
        return config;
    }

    public Configuration getConfig() {
        return config;
    }

    private void update() {
        InputStream stream = StaffPlus.get().getResourceAsStream("config.yml");
        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        try{
            if (config.getInt("config-version") < CURRENT_VERSION) {
                for (String str : configuration.getKeys()) {
                    if (!config.contains(str)){
                        config.set(str,configuration.get(str));
                    }
                }
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
