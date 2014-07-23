package jcraft.customportals;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditHandler {

    private WorldEditPlugin WORLDEDIT;

    public WorldEditHandler(CustomPortalsPlugin plugin) {
        final Plugin we = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        if (we != null) {
            WORLDEDIT = (WorldEditPlugin) we;
        } else {
            plugin.getLogger().log(Level.SEVERE, "WorldEdit was not detected! CustomPortals is now disabled!");
            plugin.getServer().getPluginManager().disablePlugin(we);
        }
    }

    public boolean hasSelected(Player player) {
        final Selection selection = WORLDEDIT.getSelection(player);
        if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
            return false;
        }
        return true;
    }

    public Selection getSelection(Player player) {
        return WORLDEDIT.getSelection(player);
    }

}
