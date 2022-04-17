package net.shortninja.staffplus.bungee.server.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.shortninja.staffplus.bungee.StaffPlus;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e){
        ProxiedPlayer p = (ProxiedPlayer)e.getSender();
        String staffChatSym = StaffPlus.get().getConfig().getString("staff-chat-module.staff-chat-handle");
        String prefix = StaffPlus.get().getConfig().getString("staff-chat-module.staff-chat-prefix");
        String format = StaffPlus.get().getConfig().getString("staff-chat-module.staff-chat-format");
        if (e.getMessage().startsWith(staffChatSym)) {
            String message = e.getMessage().substring(staffChatSym.length());
            e.setCancelled(true);
            format = format.replace("%prefix%",prefix);
            format = format.replace("%player%",p.getDisplayName());
            format = format.replace("%server%",p.getServer().getInfo().getName());
            for (ProxiedPlayer player : StaffPlus.get().getProxy().getPlayers()) {
                if (player.hasPermission(StaffPlus.get().getConfig().getString("permissions.staff-chat"))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', format + " " + message));
                }
            }
        }
    }
}
