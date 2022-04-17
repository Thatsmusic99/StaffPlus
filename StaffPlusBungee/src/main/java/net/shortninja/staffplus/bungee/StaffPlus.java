package net.shortninja.staffplus.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.shortninja.staffplus.bungee.data.config.LanguageFile;
import net.shortninja.staffplus.bungee.data.config.Options;
import net.shortninja.staffplus.bungee.server.command.StaffChatCmd;
import net.shortninja.staffplus.bungee.server.listener.ChatListener;
import net.shortninja.staffplus.bungee.util.PermissionHandler;

import java.io.*;

public final class StaffPlus extends Plugin implements Listener {

    private static Configuration config;
    private static StaffPlus instance;
    private LanguageFile lang;
    private Options options;
    private PermissionHandler permissionHandler;

    @Override
    public void onEnable() {
        instance = this;
        permissionHandler = new PermissionHandler(this);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListener());
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        Options options = new Options(this);
        config = options.setupConfig();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new StaffChatCmd(getConfig().getString("staff-chat-module.staff-chat-command")));
        //lang = new LanguageFile();

    }

    public static StaffPlus get(){
        return instance;
    }

    public Configuration getConfig(){
        return config;
    }

    public Configuration getLang(){
        return lang.get();
    }

    public LanguageFile getLangFile(){
        return lang;
    }

    public Options getOptions(){
        return options;
    }

    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getPluginManager().unregisterListener(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ChatEvent e) {

       /*if (e.isCommand()) {
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
        }*/
    }
}
