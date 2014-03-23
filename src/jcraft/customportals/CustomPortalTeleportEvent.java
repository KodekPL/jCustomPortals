package jcraft.customportals;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomPortalTeleportEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final CustomPortal portal;
    private Location target;
    private final Player player;

    private boolean isCancelled;

    public CustomPortalTeleportEvent(Player player, CustomPortal portal, Location target) {
        this.portal = portal;
        this.target = target;
        this.player = player;
    }

    public CustomPortal getPortal() {
        return portal;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getTarget() {
        return target;
    }

    public void setTarget(Location target) {
        this.target = target;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
