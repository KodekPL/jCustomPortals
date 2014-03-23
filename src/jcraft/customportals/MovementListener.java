package jcraft.customportals;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        final MovementSession session = MovementSession.getSession(player);
        if (!session.canTeleport()) {
            return;
        }
        session.setStaleLocation(player.getLocation());
        if (session.isStaleLocation()) {
            return;
        }

        final CustomPortal portal = session.getStandingInPortal();
        if (portal != null) {
            portal.teleport(player);
        }
    }
}
