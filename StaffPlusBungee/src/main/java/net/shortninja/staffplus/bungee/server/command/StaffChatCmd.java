package net.shortninja.staffplus.bungee.server.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.shortninja.staffplus.bungee.StaffPlus;

public class StaffChatCmd extends Command{

    public StaffChatCmd(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(strings.length!=0) {
            String prefix = StaffPlus.get().getConfig().getString("staff-chat-module.staff-chat-prefix");
            String format = StaffPlus.get().getConfig().getString("staff-chat-module.staff-chat-format");
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer) commandSender;
                StringBuilder message = new StringBuilder();
                for (int i = 0; i < strings.length; i++) {
                    message.append(strings[i] + " ");
                }
                format = format.replace("%prefix%",prefix);
                format = format.replace("%player%", p.getDisplayName());
                format = format.replace("%server%", p.getServer().getInfo().getName());
                for (ProxiedPlayer player : StaffPlus.get().getProxy().getPlayers()) {
                    if (player.hasPermission(StaffPlus.get().getConfig().getString("permissions.staff-chat"))) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', format+ " "+ message));
                    }
                }
            } else {
                StringBuilder message = new StringBuilder();
                for (int i = 0; i < strings.length; i++) {
                    message.append(strings[i] + " ");
                }
                String msg = message.toString();
                format = format.replace("%prefix%",prefix);
                format = format.replace("%player%", "Console");
                format = format.replace("%server%", "Console");
                for (ProxiedPlayer player : StaffPlus.get().getProxy().getPlayers()) {
                    if (player.hasPermission(StaffPlus.get().getConfig().getString("permissions.staff-chat"))) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', format + " " + msg));
                    }
                }
            }
        }else{ }
    }
}
