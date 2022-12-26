package net.shortninja.staffplus.server.command.cmd;

import me.clip.placeholderapi.PlaceholderAPI;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.server.chat.ChatHandler;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.unordered.IUser;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import net.shortninja.staffplus.util.lib.JavaUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class StaffChatCmd extends BukkitCommand {
    private PermissionHandler permission = StaffPlus.get().permission;
    private MessageCoordinator message = StaffPlus.get().message;
    private UserManager userManager = StaffPlus.get().userManager;
    private ChatHandler chatHandler = StaffPlus.get().chatHandler;

    public StaffChatCmd(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        Messages messages = StaffPlus.get().messages;
        Options options = StaffPlus.get().options;
        if (!permission.has(sender, options.permissionStaffChat)) {
            message.send(sender, messages.noPermission, messages.prefixStaffChat);
            return true;
        }

        if (args.length > 0) {
            String name = sender instanceof Player ? sender.getName() : "Console";
            chatHandler.sendStaffChatMessage(name, JavaUtils.compileWords(args, 0));
        } else if (sender instanceof Player) {
            IUser user = userManager.get(((Player) sender).getUniqueId());

            if (user.isChatting()) {
                
                message.send(sender, messages.staffChatStatus.replace("%status%", messages.disabled), messages.prefixStaffChat);
                user.setChatting(false);
            } else {
                message.send(sender, messages.staffChatStatus.replace("%status%", messages.enabled), messages.prefixStaffChat);
                user.setChatting(true);
            }
        } else message.send(sender, messages.onlyPlayers, messages.prefixStaffChat);



        return true;
    }
}