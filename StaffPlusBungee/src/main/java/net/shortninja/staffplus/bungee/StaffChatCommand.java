package net.shortninja.staffplus.bungee;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

public class StaffChatCommand extends Command{

    public StaffChatCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        StaffPlus.instance.getLogger().info("Entered command class");
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < strings.length; i++){
            message.append(strings[i]+ " ");
        }
        for(ProxiedPlayer player : StaffPlus.instance.getProxy().getPlayers()){
            StaffPlus.instance.getProxy().getLogger().info(player.getDisplayName());
            for(String str : player.getPermissions()){
                StaffPlus.instance.getProxy().getLogger().info(str);
            }
            StaffPlus.instance.getProxy().getLogger().info("");
            if(player.hasPermission("staff.staffchat")){
                StaffPlus.instance.getProxy().getLogger().info(player.getDisplayName()+ " from inside if");
                player.sendMessage(message.toString());
            }
        }
    }
}
