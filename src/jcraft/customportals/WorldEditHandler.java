package jcraft.customportals;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditHandler {

    private WorldEditPlugin worldEdit;

    public WorldEditHandler(MainClass mainClass) {
        final Plugin plugin = mainClass.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin != null) {
            worldEdit = (WorldEditPlugin) plugin;
        } else {
            mainClass.getLogger().log(Level.SEVERE, "WorldEdit was not detected! CustomPortals is now disabled!");
            mainClass.getServer().getPluginManager().disablePlugin(mainClass);
        }
    }

    public boolean hasSelected(Player player) {
        final Selection selection = worldEdit.getSelection(player);
        if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
            return false;
        }
        return true;
    }

    public Selection getSelection(Player player) {
        return worldEdit.getSelection(player);
    }

}
