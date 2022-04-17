package net.shortninja.staffplus.bungee.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Utils {

    public static String colorize(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public static String repeat(char c, int count) {
        if (count == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(c);
        }
        return builder.toString();
    }


}
