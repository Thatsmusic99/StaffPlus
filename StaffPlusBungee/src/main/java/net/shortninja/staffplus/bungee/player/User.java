package net.shortninja.staffplus.bungee.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.shortninja.staffplus.unordered.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class User {



    private final ProxiedPlayer player;

    public User(ProxiedPlayer player){
        this.player = player;
    }

    
    public UUID getUuid() {
        return player.getUniqueId();
    }

    
    public String getName() {
        return player.getName();
    }

    
    public void setVanishType(VanishType vanishType) {

    }

    
    public VanishType getVanishType() {
        return null;
    }

    
    public void setGlassColor(short color) {

    }

    
    public short getGlassColor() {
        return 0;
    }

    
    public List<IReport> getReports() {
        return null;
    }

    
    public List<IWarning> getWarnings() {
        return null;
    }

    
    public List<String> getPlayerNotes() {
        return null;
    }

    
    public boolean shouldNotify(AlertType alertType) {
        return false;
    }

    
    public void setFrozen(boolean frozen) {

    }

    
    public boolean isFrozen() {
        return false;
    }

    
    public Optional<?> getPlayer() {
        return Optional.ofNullable(player);
    }

    
    public void setCurrentGui(IGui gui) {

    }

    
    public Optional<IGui> getCurrentGui() {
        return Optional.empty();
    }

    
    public void setQueuedAction(IAction action) {

    }

    
    public void addPlayerNote(String s) {

    }

    
    public void addReport(IReport report) {

    }

    
    public void addWarning(IWarning warning) {

    }

    
    public void removeWarning(UUID uuid) {

    }

    
    public IAction getQueuedAction() {
        return null;
    }

    
    public boolean isChatting() {
        return false;
    }

    
    public void setChatting(boolean b) {

    }

    
    public void setAlertOption(AlertType alertType, boolean isEnabled) {

    }

    
    public boolean isOnline() {
        return false;
    }

    
    public void setOnline(boolean b) {

    }
}
