package jcraft.customportals;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MovementSession {

    private static final Map<String, MovementSession> moveSessions = new HashMap<String, MovementSession>();

    private final String playerName;
    private Location location;
    private boolean isStale;
    private CustomPortal standingInPortal = null;
    private long lastTeleport = -1;

    public MovementSession(String playerName, Location location) {
        this.playerName = playerName;
        this.location = location;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.setStandingInPortal();
    }

    public void setStandingInPortal() {
        final PortalWorld world = PortalWorld.getWorld(this.location.getWorld());
        if (world.hasPortals() && this.standingInPortal == null) {
            this.standingInPortal = world.getPortal(this.location);
        } else {
            this.standingInPortal = null;
        }
    }

    public CustomPortal getStandingInPortal() {
        return this.standingInPortal;
    }

    public void setStaleLocation(Location location) {
        if (this.location.getBlockX() == location.getBlockX() && this.location.getBlockY() == location.getBlockY()
                && this.location.getBlockZ() == location.getBlockZ()) {
            this.isStale = true;
        } else {
            this.setLocation(location);
            this.isStale = false;
        }
    }

    public boolean isStaleLocation() {
        return this.isStale;
    }

    public boolean canTeleport() {
        if (lastTeleport == -1) {
            lastTeleport = System.currentTimeMillis();
            return true;
        }
        if (System.currentTimeMillis() - lastTeleport >= MainClass.teleportCooldown) {
            lastTeleport = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public static MovementSession getSession(Player player) {
        if (moveSessions.containsKey(player.getName())) {
            return moveSessions.get(player.getName());
        }
        final MovementSession session = new MovementSession(player.getName(), player.getLocation());
        moveSessions.put(player.getName(), session);
        return session;

    }

}
