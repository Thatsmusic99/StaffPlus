package net.shortninja.staffplus;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.shortninja.staffplus.player.NodeUser;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.player.attribute.SecurityHandler;
import net.shortninja.staffplus.player.attribute.TicketHandler;
import net.shortninja.staffplus.player.attribute.infraction.InfractionCoordinator;
import net.shortninja.staffplus.player.attribute.mode.ModeCoordinator;
import net.shortninja.staffplus.player.attribute.mode.handler.*;
import net.shortninja.staffplus.server.AlertCoordinator;
import net.shortninja.staffplus.server.PacketModifier;
import net.shortninja.staffplus.server.chat.ChatHandler;
import net.shortninja.staffplus.server.command.CmdHandler;

import net.shortninja.staffplus.server.compatibility.IProtocol;
import net.shortninja.staffplus.server.data.*;
import net.shortninja.staffplus.server.data.storage.FlatFileStorage;
import net.shortninja.staffplus.server.data.storage.IStorage;
import net.shortninja.staffplus.server.data.storage.MemoryStorage;
import net.shortninja.staffplus.server.data.storage.MySQLStorage;
import net.shortninja.staffplus.server.data.config.IOptions;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.server.data.file.ChangelogFile;
import net.shortninja.staffplus.server.data.file.DataFile;
import net.shortninja.staffplus.server.data.file.LanguageFile;
import net.shortninja.staffplus.server.hook.HookHandler;
import net.shortninja.staffplus.server.hook.PAPIExpansion;
import net.shortninja.staffplus.server.hook.SuperVanishHook;
import net.shortninja.staffplus.server.listener.*;
import net.shortninja.staffplus.server.listener.entity.EntityChangeBlock;
import net.shortninja.staffplus.server.listener.entity.EntityDamage;
import net.shortninja.staffplus.server.listener.entity.EntityDamageByEntity;
import net.shortninja.staffplus.server.listener.entity.EntityTarget;
import net.shortninja.staffplus.server.listener.player.*;
import net.shortninja.staffplus.server.nms.PacketHandler;
import net.shortninja.staffplus.unordered.IUser;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.Metrics;
import net.shortninja.staffplus.util.PermissionHandler;
import net.shortninja.staffplus.util.lib.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

// TODO Add command to check e chests and offline player inventories

public class StaffPlus extends JavaPlugin implements IStaffPlus {
    private static StaffPlus plugin;

    public IProtocol versionProtocol;
    public PermissionHandler permission;
    public MessageCoordinator message;
    public Options options;
    public DataFile dataFile;
    public LanguageFile languageFile;
    public Messages messages;
    public UserManager userManager;

    public HookHandler hookHandler;
    public CpsHandler cpsHandler;
    public FreezeHandler freezeHandler;
    public GadgetHandler gadgetHandler;
    public ReviveHandler reviveHandler;
    public VanishHandler vanishHandler;
    public ChatHandler chatHandler;
    public TicketHandler ticketHandler;
    public CmdHandler cmdHandler;
    public ModeCoordinator modeCoordinator;
    public SecurityHandler securityHandler;
    public InfractionCoordinator infractionCoordinator;
    public AlertCoordinator alertCoordinator;
    public UUID consoleUUID = UUID.fromString("9c417515-22bc-46b8-be4d-538482992f8f");
    public Tasks tasks;
    public Map<UUID, IUser> users;
    public ProtocolManager protocolManager;
    private MySQLConnection mySQLConnection;
    public boolean ninePlus = false;
    public HashMap<Inventory, Block> viewedChest = new HashMap<>();
    public boolean twelvePlus = false;
    public boolean thirteenPlus = false;
    public IStorage storage;
    public InventoryHandler inventoryHandler;
    public boolean usesPlaceholderAPI;

    public static StaffPlus get() {
        return plugin;
    }

    @Override
    public void onLoad() {

        Bukkit.getLogger().setFilter(new PasswordFilter()); // FIXME
        plugin = this;
        Plugin placeholderPlugin;
        if ((placeholderPlugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI")) != null) {
            usesPlaceholderAPI = true;
            new PAPIExpansion().register();
            Bukkit.getLogger().info("Hooked into PlaceholderAPI " + placeholderPlugin.getDescription().getVersion());
        }
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        permission = new PermissionHandler(this);
        message = new MessageCoordinator(this);
        options = new Options();
        start(System.currentTimeMillis());
        if (options.storageType.equalsIgnoreCase("mysql")) {
            storage = new MySQLStorage(new MySQLConnection());
        } else if (options.storageType.equalsIgnoreCase("flatfile"))
            storage = new FlatFileStorage();
        else {
            storage = new MemoryStorage();
            Bukkit.getLogger().warning("Storage type is invalid, defaulting to memory-based storage. IMPORTANT: Any changes are not persistent.");
        }


        if (getConfig().getBoolean("metrics"))
            new Metrics(this);
        checkUpdate();

        storage.onEnable();

        hookHandler.addHook(new SuperVanishHook(this));
        hookHandler.enableAll();
    }


    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public void onDisable() {
        message.sendConsoleMessage("Staff+ is now disabling!", true);
        stop();
    }

    public void saveUsers() {
        for (IUser user : userManager.getAll()) {
            new Save(new NodeUser(user));
        }

        dataFile.save();
    }

    public IStorage getStorage() {
        return storage;
    }

    protected void start(long start) {
        users = new HashMap<>();
        if (!setupVersionProtocol()) {
            message.sendConsoleMessage("This version of Minecraft is not supported! If you have just updated to a brand new server version, check the Spigot plugin page.", true);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        String[] tmp = Bukkit.getServer().getVersion().split("MC: ");
        String version = tmp[tmp.length - 1].substring(0, 4);
        ninePlus = JavaUtils.parseMcVer(version) >= 9;
        twelvePlus = JavaUtils.parseMcVer(version) >= 12;
        thirteenPlus = JavaUtils.parseMcVer(version) >= 13;
        dataFile = new DataFile("data.yml");
        languageFile = new LanguageFile();
        messages = new Messages();
        userManager = new UserManager(this);
        securityHandler = new SecurityHandler(); // FIXME
        hookHandler = new HookHandler();
        cpsHandler = new CpsHandler();
        freezeHandler = new FreezeHandler();
        gadgetHandler = new GadgetHandler();
        reviveHandler = new ReviveHandler();
        vanishHandler = new VanishHandler();
        chatHandler = new ChatHandler();
        ticketHandler = new TicketHandler();
        cmdHandler = new CmdHandler();
        modeCoordinator = new ModeCoordinator();
        infractionCoordinator = new InfractionCoordinator();
        alertCoordinator = new AlertCoordinator();
        tasks = new Tasks();
        inventoryHandler = new InventoryHandler();
        for (Player player : Bukkit.getOnlinePlayers()) {
            new Load(player);
        }
        registerListeners();
        new ChangelogFile();

        if (!options.disablePackets || !options.animationPackets.isEmpty() || !options.soundNames.isEmpty()) {
            new PacketModifier();
        }

        message.sendConsoleMessage("Staff+ has been enabled! Initialization took " + (System.currentTimeMillis() - start) + "ms.", false);
        message.sendConsoleMessage("Plugin created by Shortninja continued by Qball.", false);
    }

    private boolean setupVersionProtocol() {

        // Get ProtocolLib
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketHandler(this));
        return true;
    }

    private void registerListeners() {
        new EntityDamage();
        new EntityDamageByEntity();
        new EntityTarget();
        new AsyncPlayerChat();
        new PlayerCommandPreprocess();
        new PlayerDeath();
        new PlayerDropItem();
        new PlayerInteract();
        new PlayerLogin();
        new PlayerJoin();
        new PlayerPickupItem();
        new PlayerQuit();
        new BlockBreak();
        new BlockPlace();
        new FoodLevelChange();
        new InventoryClick();
        new InventoryClose();
        new InventoryOpen();
        new PlayerWorldChange();
        new EntityChangeBlock();
        String[] tmp = Bukkit.getServer().getVersion().split("MC: ");
        String version = tmp[tmp.length - 1].substring(0, 4);
        if (JavaUtils.parseMcVer(version) >= 10)
            new TabComplete();
    }

    private void checkUpdate() {
        SpigetUpdate updater = new SpigetUpdate(this, 41500);
        updater.setVersionComparator(VersionComparator.SEM_VER);
        updater.checkForUpdate(new UpdateCallback() {
            @Override
            public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
                if (options.autoUpdate) {
                    if (hasDirectDownload) {
                        if (updater.downloadUpdate()) {
                            getLogger().info("New version of the plugin downloaded and will be loaded on restart");
                        } else {
                            getLogger().warning("Update download failed, reason is " + updater.getFailReason());
                        }
                    }
                } else {
                    getLogger().info("There is an update available please go download it");
                }
            }

            @Override
            public void upToDate() {
                getLogger().info("You are using the latest version thanks");
            }
        });
    }


    /*
     * Nullifying all of the instances is sort of an experimental thing to deal
     * with memory leaks that could occur on reloads (where instances could be
     * handled incorrectly)
     */


    private void stop() {
        hookHandler.disableAll();

        saveUsers();
        tasks.cancel();

        for (Player player : Bukkit.getOnlinePlayers()) {
            modeCoordinator.removeMode(player);
            vanishHandler.removeVanish(player);
        }

        storage.onDisable();

        versionProtocol = null;
        permission = null;
        message = null;
        options = null;
        languageFile = null;
        userManager = null;
        //securityHandler = null; // FIXME
        cpsHandler = null;
        freezeHandler = null;
        gadgetHandler = null;
        reviveHandler = null;
        vanishHandler = null;
        chatHandler = null;
        ticketHandler = null;
        cmdHandler = null;
        modeCoordinator = null;
        infractionCoordinator = null;
        alertCoordinator = null;
        tasks = null;
        plugin = null;

    }

    @Override
    public IOptions getOptions() {
        return options;
    }

    public void reloadFiles() {
        /*options = options.reloadConfig();
        languageFile = new LanguageFile();
        messages = new Messages();
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(languageFile.getLangFile());
        for(String s : langConfig.getConfigurationSection("").getKeys(false)){
            System.out.println(langConfig.get(s));
        }*/
        YamlConfiguration configuration = new Options().getConfiguration();
        LanguageFile languageFile = new LanguageFile();
        YamlConfiguration languageConfig = (YamlConfiguration) languageFile.get();
        languageConfig.getConfigurationSection("").getKeys(false).forEach( s -> {
            getLogger().info(languageConfig.get(s).toString());
        });
        this.languageFile = languageFile;
        this.messages = new Messages();

    }

    public PermissionHandler getPermissions() {
        return permission;
    }


    private static final class PasswordFilter implements Filter {

        @Override
        public boolean isLoggable(LogRecord record) {
            return !record.getMessage().toLowerCase().contains("/register") && !record.getMessage().toLowerCase().contains("/login");
        }
    }
}