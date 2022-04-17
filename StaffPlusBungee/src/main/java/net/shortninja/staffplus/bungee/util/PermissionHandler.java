package net.shortninja.staffplus.bungee.util;

import net.shortninja.staffplus.bungee.StaffPlus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionHandler {

    private StaffPlus staffPlus;

    public PermissionHandler(StaffPlus staffPlus) {

        this.staffPlus = staffPlus;
    }

    public boolean has(Player player, String permission) {
        boolean hasPermission = false;

        if (player != null) {
            hasPermission = player.hasPermission(permission) || isOp(player);
        }

        return hasPermission;
    }

    public boolean hasOnly(Player player, String permission) {
        boolean hasPermission = false;

        if (player != null) {
            hasPermission = player.hasPermission(permission) && !player.isOp();
        }

        return hasPermission;
    }

    public boolean has(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || isOp(sender);
    }

    public boolean isOp(Player player) {
        return player.hasPermission(staffPlus.getOptions().getConfig().getString("permissions.wild-card"));
    }

    public boolean isOp(CommandSender sender) {
        return sender.hasPermission(staffPlus.getOptions().getConfig().getString("permissions.wild-card"));
    }

}
