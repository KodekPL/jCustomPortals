package jcraft.customportals;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {

    public final static String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "jPortal" + ChatColor.GOLD + "] ";
    private final static File configFile = new File("plugins" + File.separator + "jCustomPortals" + File.separator + "config.yml");

    private static WorldEditHandler worldEdit;
    public static long teleportCooldown;
    public static boolean checkPermissions;

    public void onEnable() {
        worldEdit = new WorldEditHandler(this);

        genConfig();
        loadConfig();
        CustomPortal.loadDestinations();
        CustomPortal.loadPortals();

        this.getCommand("jportal").setExecutor(new CommandsHandler(this));
        this.getServer().getPluginManager().registerEvents(new MovementListener(), this);
    }

    public void genConfig() {
        if (!configFile.exists()) {
            final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(configFile);
            ymlConfig.set("TeleportCooldown", 1000L);
            ymlConfig.set("CheckPermissions", true);
            try {
                ymlConfig.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadConfig() {
        final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(configFile);
        checkPermissions = ymlConfig.getBoolean("CheckPermissions", true);
        teleportCooldown = ymlConfig.getLong("TeleportCooldown", 1000L);
    }

    public static WorldEditHandler getWorldEdit() {
        return worldEdit;
    }

}
