package net.shortninja.staffplus.server.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.shortninja.staffplus.StaffPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

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
        if(params.equalsIgnoreCase("num_vanished")){
            return String.valueOf(StaffPlus.get().vanishHandler.getVanished().size());
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
        if(params.equalsIgnoreCase("num_staff_online)")){
            AtomicInteger i = new AtomicInteger();
            StaffPlus.get().users.values().forEach((usr -> {
                if(StaffPlus.get().permission.has(usr.getPlayer().get(),StaffPlus.get().options.permissionMode)){
                    i.getAndIncrement();
                }
            }));
            StaffPlus.get().users.values().stream().filter(iUser -> StaffPlus.get().permission.has(iUser.getPlayer().get(),StaffPlus.get().options.permissionMode)).count();
            return String.valueOf(i);
        }
        if(params.equalsIgnoreCase("online")){
            StringBuilder sb = new StringBuilder();
            StaffPlus.get().users.values().forEach(usr -> {
                if(!usr.isVanished())
                    sb.append(usr.getName());
            });
            return sb.toString();
        }
        if(params.equalsIgnoreCase("num_online")){
            int i = (int)StaffPlus.get().users.values().stream().filter(usr -> !usr.isVanished()).count();

            return String.valueOf(i);
        }
        return null;
    }
}
