package jcraft.customportals.event;

import jcraft.customportals.CustomPortal;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JCPortalEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private CustomPortal portal;
    private Player p;

    private boolean cancelled;

    public JCPortalEvent(Player player, CustomPortal customportal) {
        portal = customportal;
        p = player;
    }

    public CustomPortal getPortal() {
        return portal;
    }

    public Player getPlayer(){
        return p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
