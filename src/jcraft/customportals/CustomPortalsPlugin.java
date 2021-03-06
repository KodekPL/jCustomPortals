package jcraft.customportals;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPortalsPlugin extends JavaPlugin {

    public final static String PREFIX = ChatColor.GOLD + "[" + ChatColor.GREEN + "jPortal" + ChatColor.GOLD + "] ";
    public static File CONFIG_FILE, PORTALS_FILE, DESTS_FILE;

    private static WorldEditHandler WORLDEDIT_PLUGIN;

    public static long teleportCooldown;
    public static boolean checkPermissions;
    public static boolean resetFalling;

    public void onEnable() {
        CONFIG_FILE = new File(this.getDataFolder(), "config.yml");
        PORTALS_FILE = new File(this.getDataFolder(), "portals.yml");
        DESTS_FILE = new File(this.getDataFolder(), "destinations.yml");

        WORLDEDIT_PLUGIN = new WorldEditHandler(this);

        genConfig();
        loadConfig();
        CustomPortal.loadDestinations();
        CustomPortal.loadPortals();

        this.getCommand("jportal").setExecutor(new CommandsHandler(this));
        this.getServer().getPluginManager().registerEvents(new MovementListener(), this);
    }

    public void genConfig() {
        if (!CONFIG_FILE.exists()) {
            final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(CONFIG_FILE);
            ymlConfig.set("TeleportCooldown", 500L);
            ymlConfig.set("CheckPermissions", true);
            ymlConfig.set("ResetFalling", true);
            try {
                ymlConfig.save(CONFIG_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadConfig() {
        final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(CONFIG_FILE);
        teleportCooldown = ymlConfig.getLong("TeleportCooldown", 500L);
        checkPermissions = ymlConfig.getBoolean("CheckPermissions", true);
        resetFalling = ymlConfig.getBoolean("ResetFalling", true);
    }

    public static WorldEditHandler getWorldEdit() {
        return WORLDEDIT_PLUGIN;
    }

}
