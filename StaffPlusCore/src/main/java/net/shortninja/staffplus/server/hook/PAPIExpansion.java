package net.shortninja.staffplus.server.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PAPIExpansion extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "staffplus";
    }

    @Override
    public String getAuthor() {
        return "Shortninja, Qball, and Azoraqua";
    }

    @Override
    public String getVersion() {
        return StaffPlus.get().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onPlaceholderRequest(Player p, String params){
        if(params.equalsIgnoreCase("staff_in_mode")){
            StringBuilder sb = new StringBuilder();
            sb.append("Staff currently in mode: ");
            StaffPlus.get().modeCoordinator.getModeUsers().stream().forEach(user -> sb.append(Bukkit.getPlayer(user).getName()+", ") );
            return sb.toString();
        }
        if(params.equalsIgnoreCase("vanished")){
            StringBuilder sb = new StringBuilder();
            sb.append("Users currently vanished: ");
            StaffPlus.get().vanishHandler.getVanished().forEach(user -> sb.append(user.getName()+" "));
            return sb.toString();
        }
        if(params.equalsIgnoreCase("staff_online")){
            StringBuilder sb = new StringBuilder();
            sb.append("Staff currently online: ");
            StaffPlus.get().users.values().forEach(usr -> {
                if(StaffPlus.get().permission.has(usr.getPlayer().get(),StaffPlus.get().options.permissionMode))
                    sb.append(usr.getName()+" ");
            });
            return sb.toString();
        }
        return null;
    }
}
