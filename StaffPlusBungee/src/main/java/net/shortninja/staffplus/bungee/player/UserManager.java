package net.shortninja.staffplus.bungee.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.shortninja.staffplus.unordered.IUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private Map<UUID, User> users;

    public UserManager(){
        users = new HashMap<>();
    }

    public void addUser(ProxiedPlayer player){
        users.put(player.getUniqueId(), new User(player));
    }

}
