package jcraft.customportals;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (from == null || to == null) {
            return;
        }

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        final PortalWorld world = PortalWorld.getWorld(to.getWorld());
        if (!world.hasPortals()) {
            return;
        }

        if (!TeleportSessions.canTeleport(player.getUniqueId())) {
            return;
        }

        final CustomPortal portal = world.getPortal(to);
        if (portal != null) {
            portal.teleport(player);
        }
    }

}
