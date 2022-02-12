package net.shortninja.staffplus.server.chat;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatHandler {
    private static Map<UUID, Long> userChatTimes = new HashMap<UUID, Long>();
    private PermissionHandler permission = StaffPlus.get().permission;
    private MessageCoordinator message = StaffPlus.get().message;

    private boolean isChatEnabled = true;
    private long chatSlowLength = 0;
    private long chatSlowStart = 0;

    public boolean isChatEnabled() {
        return isChatEnabled;
    }

    public boolean isChatEnabled(Player player) {
        return isChatEnabled || permission.has(player, StaffPlus.get().options.permissionChatToggle);
    }

    public boolean canChat(Player player) {
        Options options = StaffPlus.get().options;
        boolean canChat = true;

        if (chatSlowLength > 0 && !permission.has(player, options.permissionChatSlow)) {
            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();
            long lastChat = userChatTimes.containsKey(uuid) ? userChatTimes.get(uuid) : 0;

            if ((now - chatSlowStart) >= chatSlowLength) {
                chatSlowLength = 0;
                chatSlowStart = 0;
                userChatTimes.clear();
            } else if (((now - lastChat) / 1000) <= options.chatSlow) {
                canChat = false;
            } else userChatTimes.put(uuid, now);
        }

        return canChat;
    }

    public boolean hasHandle(String message) {
        return message.startsWith(StaffPlus.get().options.staffChatHandle) && !StaffPlus.get().options.staffChatHandle.isEmpty();
    }

    public void setChatEnabled(String name, boolean isChatEnabled) {
        String status = isChatEnabled ? "enabled" : "disabled";

        message.sendGlobalMessage(StaffPlus.get().messages.chatToggled.replace("%status%", status).replace("%player%", name), StaffPlus.get().messages.prefixGeneral);
        this.isChatEnabled = isChatEnabled;
    }

    public void setChatSlow(String name, int time) {
        chatSlowLength = time * 1000;
        chatSlowStart = System.currentTimeMillis();
        message.sendGlobalMessage(StaffPlus.get().messages.chatSlowed.replace("%seconds%", Integer.toString(time)).replace("%player%", name), StaffPlus.get().messages.prefixGeneral);
    }

    public void sendStaffChatMessage(String name, String message) {
        this.message.sendGroupMessage(StaffPlus.get().messages.staffChat.replace("%player%", name).replace("%message%", message),
                StaffPlus.get().options.permissionStaffChat, StaffPlus.get().messages.prefixStaffChat);
    }

    public void clearChat(String name) {
        Options options = StaffPlus.get().options;
        Messages messages = StaffPlus.get().messages;
        for (int i = 0; i < options.chatLines; i++) {
            message.sendGlobalMessage(messages.chatClearLine, "");
        }

        message.sendGlobalMessage(messages.chatCleared.replace("%player%", name), messages.prefixGeneral);
    }
}