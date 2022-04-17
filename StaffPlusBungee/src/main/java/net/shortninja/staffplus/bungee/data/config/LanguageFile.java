package net.shortninja.staffplus.bungee.data.config;

import net.md_5.bungee.config.Configuration;

import net.md_5.bungee.config.YamlConfiguration;
import net.shortninja.staffplus.bungee.StaffPlus;

import java.io.*;

public class LanguageFile {
    private static final String[] LANG_FILES =
            {
                    "lang_en", "lang_sv", "lang_de", "lang_nl", "lang_es", "lang_hr", "lang_no", "lang_fr", "lang_hu", "lang_it",
                    "lang_zh", "lang_pt"
            };
    private final String FILE_NAME = StaffPlus.get().getConfig().getString("lang") + ".yml";
    private Configuration lang;
    private File langFile;

    public LanguageFile() {
        for (String fileName : LANG_FILES) {
            try {
                copyFile(fileName);
            } catch (Exception exception) {
                System.out.println(exception);
                StaffPlus.get().getLogger().info("Error occured while initializing '" + fileName + "'!");
            }
        }

        setup();
    }

    public void setup() {
        langFile = new File(StaffPlus.get().getDataFolder() + "/lang/", FILE_NAME);
        try {
            lang = YamlConfiguration.getProvider(YamlConfiguration.class).load(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Configuration get() {
        return lang;
    }

    private void copyFile(String fileName) throws IOException {
        File file = new File(StaffPlus.get().getDataFolder() + "/lang/", fileName + ".yml");
        InputStream in = this.getClass().getResourceAsStream("/lang/" + fileName + ".yml");
        Configuration newLang = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(in));
        in = this.getClass().getResourceAsStream("/lang/" + fileName + ".yml");
        if (!file.exists()) {
            StaffPlus.get().getDataFolder().mkdirs();
            file.getParentFile().mkdirs();
            file.createNewFile();
            StaffPlus.get().getLogger().info("Creating language file '" + fileName + "'.");
        } else if (YamlConfiguration.getProvider(YamlConfiguration.class).load(file).getInt("lang-version") <
                newLang.getInt("lang-version")) {
            Configuration oldLang = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
            for (String key : newLang.getKeys()) {
                if (oldLang.getString(key, "").equals("")) {
                    oldLang.set(key, newLang.get(key));
                    save(file);
                    //oldLang.save(file);
                }
            }
            oldLang.set("lang-version", newLang.getInt("lang-version"));
            save(file);
            //oldLang.save(file);
            in.close();
            return;
        } else
            return;

        OutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int current = 0;

        while ((current = in.read(buffer)) > -1) {
            out.write(buffer, 0, current);
        }

        out.close();
        in.close();
    }
    //TODO MAKE CONFIG UPDATING WORK WITH BUNGEE ADD OPTION TO THE CONFIG FOR STAFFCHAT-FORMAT
    private void save(File file)  {
        try {
            OutputStream stream = new FileOutputStream(file);
            Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
            for(String str : config.getKeys()){
                stream.write(str.getBytes());
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getLangFile() {
        return langFile;
    }
}
