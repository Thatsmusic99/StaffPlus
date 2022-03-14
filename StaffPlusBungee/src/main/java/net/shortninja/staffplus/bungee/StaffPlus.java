package net.shortninja.staffplus.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.*;
import java.util.Arrays;

public final class StaffPlus extends Plugin implements Listener {

    public static Configuration config;
    public static StaffPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
       // getProxy().getPluginManager().registerCommand(this,new StaffChatCommand("sc"));
        File configFile = new File(getDataFolder(), "config.yml");
        getProxy().
        try {
            if (!configFile.exists()) {
                InputStream is = getResourceAsStream("config.yml");
                configFile.createNewFile();
                OutputStream os = new FileOutputStream(configFile);
                ByteStreams.copy(is, os);
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(),"config.yml"));
                getLogger().info(config.getString("commands.staff-chat"));
                ProxyServer.getInstance().getPluginManager().registerCommand(this,new StaffChatCommand(config.getString("commands.staff-chat")));
                getProxy().getPluginManager().registerCommand(this, new StaffChatCommand(config.getString("commands.staff-chat")));
            }else{
                getLogger().info("Should load command");
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(),"config.yml"));
                getLogger().info(config.getString("commands.staff-chat"));
                ProxyServer.getInstance().getPluginManager().registerCommand(this,new StaffChatCommand(config.getString("commands.staff-chat")));
                getProxy().getPluginManager().registerCommand(this,new StaffChatCommand(config.getString("commands.staff-chat")));
            }
        } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }

    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getPluginManager().unregisterListener(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ChatEvent e) {

        if (e.isCommand()) {
            final String[] split = e.getMessage().split(" ");
            final String command = split[0].substring(1);

            // TODO: Allow configurable command.
            if (command.equalsIgnoreCase("report")) {
                if (split.length > 1) {
                    final String[] args = Arrays.copyOfRange(split, 1, split.length);

                    if (args.length > 0) {
                        final String name = args[0];
                        final String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                        ProxyServer.getInstance().getPlayers().forEach(p -> {
                            // TODO: Allow configurable message.
                            p.sendMessage(TextComponent.fromLegacyText(String.format("§b%s§e is reported by §b%s§e with reason \"§b%s§e\".",
                                    name,
                                    ((ProxiedPlayer) e.getSender()).getName(),
                                    reason
                            )));
                        });
                    } else {
                        // TODO: Allow configurable message.
                        ((ProxiedPlayer) e.getSender()).sendMessage(TextComponent.fromLegacyText("§cPlease specify a reason."));
                    }
                } else {
                    // TODO: Allow configurable message.
                    ((ProxiedPlayer) e.getSender()).sendMessage(TextComponent.fromLegacyText("§cPlease specify a player."));
                }
            }
        }
    }
}
