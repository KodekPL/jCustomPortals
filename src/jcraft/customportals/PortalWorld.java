package jcraft.customportals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class PortalWorld {

    private static final Collection<CustomPortal> EMPTY_PORTAL_SET = new HashSet<CustomPortal>();

    private static final Map<World, PortalWorld> PORTAL_WORLDS = new HashMap<World, PortalWorld>();
    private static final Map<String, Location> DESTINATIONS = new HashMap<String, Location>();

    private final Map<String, CustomPortal> PORTALS;
    private Map<Integer, Collection<CustomPortal>> PORTAL_CHUNKS;

    public PortalWorld() {
        PORTALS = new HashMap<String, CustomPortal>();
    }

    /*************/
    /** PORTALS **/
    /*************/

    public CustomPortal getPortalByName(String name) {
        if (!PORTALS.containsKey(name)) {
            return null;
        }
        return PORTALS.get(name);
    }

    public CustomPortal getPortal(Location location) {
        for (CustomPortal portal : getNearbyPortals(location)) {
            PortalLocation portalLoc = portal.getLocation();
            if (portalLoc.intersect(location)) {
                return portal;
            }
        }
        return null;
    }

    private Collection<CustomPortal> getNearbyPortals(Location location) {
        Collection<CustomPortal> nearbyPortals = null;

        if (PORTAL_CHUNKS != null) {
            final int cx = blockToChunk(location.getBlockX());
            final int cz = blockToChunk(location.getBlockZ());
            final Integer hash = hashChunk(cx, cz);

            nearbyPortals = PORTAL_CHUNKS.get(hash);
        }
        return (nearbyPortals != null) ? nearbyPortals : EMPTY_PORTAL_SET;
    }

    public void addPortal(String name, CustomPortal portal) {
        if (this.PORTAL_CHUNKS == null) {
            this.PORTAL_CHUNKS = new HashMap<Integer, Collection<CustomPortal>>();
        }

        final PortalLocation location = portal.getLocation();
        final Vector min = location.getMinimum();
        final Vector max = location.getMaximum();
        final int c1x = blockToChunk(min.getBlockX());
        final int c1z = blockToChunk(min.getBlockZ());
        final int c2x = blockToChunk(max.getBlockX());
        final int c2z = blockToChunk(max.getBlockZ());
        for (int cx = c1x; cx <= c2x; cx++) {
            for (int cz = c1z; cz <= c2z; cz++) {
                Integer hashCode = hashChunk(cx, cz);
                Collection<CustomPortal> portals = PORTAL_CHUNKS.get(hashCode);
                if (portals == null) {
                    portals = new ArrayList<CustomPortal>();
                    PORTAL_CHUNKS.put(hashCode, portals);
                }
                portals.add(portal);
            }
        }
        PORTALS.put(name, portal);
    }

    public void removePortal(String name) {
        if (PORTAL_CHUNKS == null) {
            return;
        }

        final CustomPortal portal = PORTALS.get(name);
        final PortalLocation location = portal.getLocation();
        final Vector min = location.getMinimum();
        final Vector max = location.getMaximum();
        int c1x = blockToChunk(min.getBlockX()), c1z = blockToChunk(min.getBlockZ());
        int c2x = blockToChunk(max.getBlockX()), c2z = blockToChunk(max.getBlockZ());

        for (int cx = c1x; cx <= c2x; cx++) {
            for (int cz = c1z; cz <= c2z; cz++) {
                Integer hashCode = hashChunk(cx, cz);
                PORTAL_CHUNKS.get(hashCode).remove(portal);
            }
        }
        PORTALS.remove(name);
    }

    private int hashChunk(int cx, int cz) {
        return (cx << 16) | (cz & 0xFFFF);
    }

    private int blockToChunk(int b) {
        if (b < 0) {
            b -= 16;
        }
        return b / 16;
    }

    public Collection<CustomPortal> getPortals() {
        return PORTALS.values();
    }

    public boolean hasPortals() {
        return !PORTALS.isEmpty();
    }

    /******************/
    /** DESTINATIONS **/
    /******************/

    public static Location getDestination(String name) {
        if (!DESTINATIONS.containsKey(name)) {
            return null;
        }
        return DESTINATIONS.get(name);
    }

    public static void addDestination(String name, Location destination) {
        DESTINATIONS.put(name, destination);
    }

    public static void removeDestination(String name) {
        DESTINATIONS.remove(name);
    }

    public static Set<String> getDestinations() {
        return DESTINATIONS.keySet();
    }

    public static void clearDestinations() {
        DESTINATIONS.clear();
    }

    public static List<CustomPortal> getLinkedPortals(String destination) {
        final List<CustomPortal> portals = new ArrayList<CustomPortal>();
        for (PortalWorld portalWorld : PORTAL_WORLDS.values()) {
            for (CustomPortal portal : portalWorld.getPortals()) {
                if (portal.getDestination().equalsIgnoreCase(destination)) {
                    portals.add(portal);
                }
            }
        }
        return portals;
    }

    private static PortalWorld addWorld(World world) {
        final PortalWorld portalWorld = new PortalWorld();
        PORTAL_WORLDS.put(world, portalWorld);
        return portalWorld;
    }

    public static PortalWorld getWorld(World world) {
        if (PORTAL_WORLDS.containsKey(world)) {
            return PORTAL_WORLDS.get(world);
        } else {
            return addWorld(world);
        }
    }

    public static Collection<PortalWorld> getPortalWorlds() {
        return PORTAL_WORLDS.values();
    }

    public static void clearWorldPortals() {
        PORTAL_WORLDS.clear();
    }

}
