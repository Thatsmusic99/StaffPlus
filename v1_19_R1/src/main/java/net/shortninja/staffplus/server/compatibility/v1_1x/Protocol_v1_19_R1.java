package net.shortninja.staffplus.server.compatibility.v1_1x;

import com.google.gson.JsonElement;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;

import net.minecraft.util.Crypt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shortninja.staffplus.IStaffPlus;
import net.shortninja.staffplus.server.compatibility.AbstractProtocol;
import net.shortninja.staffplus.server.compatibility.IProtocol;
import net.shortninja.staffplus.util.lib.json.JsonMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;


import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Protocol_v1_19_R1 extends AbstractProtocol implements IProtocol {
    public Protocol_v1_19_R1(IStaffPlus staffPlus) {
        super(staffPlus);
    }

    @Override
    public org.bukkit.inventory.ItemStack addNbtString(org.bukkit.inventory.ItemStack item, String value) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        CompoundTag nbtCompound = craftItem.getTag() == null ? new CompoundTag() : craftItem.getTag();

        nbtCompound.putString(NBT_IDENTIFIER, value);
        craftItem.setTag(nbtCompound);

        return CraftItemStack.asCraftMirror(craftItem);
    }

    @Override
    public String getNbtString(org.bukkit.inventory.ItemStack item) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (craftItem == null) {
            return "";
        }

        CompoundTag nbtCompound = craftItem.getTag() == null ? new CompoundTag() : craftItem.getTag();

        return nbtCompound.getString(NBT_IDENTIFIER);
        //return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("StaffPlus","staff"),PersistentDataType.STRING);
    }

    @Override
    public void registerCommand(String match, Command command) {
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(match, command);
    }




    @Override
    public String getSound(Object object) {
        return null;
    }

    @Override
    public void listVanish(Player player, boolean shouldEnable) {
        ClientboundPlayerInfoPacket packet = null;

        if (shouldEnable) {
            packet = new ClientboundPlayerInfoPacket(Action.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());
        } else
            packet = new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, ((CraftPlayer) player).getHandle());

        sendGlobalPacket(packet);
    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
        ClientboundPlayerChatPacket packet = new ClientboundPlayerChatPacket(Component.Serializer.fromJson(json.getMessage()), Optional.empty(), 0, ChatSender.system(Component.literal("Staff+")), Instant.now(), Crypt.SaltSignaturePair.EMPTY);

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.connection.send(packet);
        }
    }


    private void sendGlobalPacket(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().connection.connection.send(packet);

        }
    }

    @Override
    public void inject(Player player) {
        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getUniqueId().toString(), new PacketHandler_v1_19_R1(player));
    }

    @Override
    public void uninject(Player player) {
        final Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }
}