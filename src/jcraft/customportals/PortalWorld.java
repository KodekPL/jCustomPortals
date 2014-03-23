package jcraft.customportals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class PortalWorld {

    private static final Collection<CustomPortal> emptyPortalSet = new ArrayList<CustomPortal>();

    private static Map<World, PortalWorld> portalWorlds = new HashMap<World, PortalWorld>();
    private static Map<String, Location> destinations = new HashMap<String, Location>();

    private Map<String, CustomPortal> portals;
    private Map<Integer, Collection<CustomPortal>> chunksPortals;

    public PortalWorld() {
        portals = new HashMap<String, CustomPortal>();
    }

    /*************/
    /** PORTALS **/
    /*************/

    public CustomPortal getPortalByName(String name) {
        if (!portals.containsKey(name)) {
            return null;
        }
        return portals.get(name);
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

        if (chunksPortals != null) {
            final int cx = blockToChunk(location.getBlockX());
            final int cz = blockToChunk(location.getBlockZ());
            final Integer hash = hashChunk(cx, cz);

            nearbyPortals = chunksPortals.get(hash);
        }
        return (nearbyPortals != null) ? nearbyPortals : emptyPortalSet;
    }

    public void addPortal(String name, CustomPortal portal) {
        if (this.chunksPortals == null) {
            this.chunksPortals = new HashMap<Integer, Collection<CustomPortal>>();
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
                Collection<CustomPortal> portals = chunksPortals.get(hashCode);
                if (portals == null) {
                    portals = new ArrayList<CustomPortal>();
                    chunksPortals.put(hashCode, portals);
                }
                portals.add(portal);
            }
        }
        portals.put(name, portal);
    }

    public void removePortal(String name) {
        if (chunksPortals == null) {
            return;
        }

        final CustomPortal portal = portals.get(name);
        final PortalLocation location = portal.getLocation();
        final Vector min = location.getMinimum();
        final Vector max = location.getMaximum();
        int c1x = blockToChunk(min.getBlockX()), c1z = blockToChunk(min.getBlockZ());
        int c2x = blockToChunk(max.getBlockX()), c2z = blockToChunk(max.getBlockZ());

        for (int cx = c1x; cx <= c2x; cx++) {
            for (int cz = c1z; cz <= c2z; cz++) {
                Integer hashCode = hashChunk(cx, cz);
                chunksPortals.get(hashCode).remove(portal);
            }
        }
        portals.remove(name);
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
        return portals.values();
    }

    public boolean hasPortals() {
        return !portals.isEmpty();
    }

    /******************/
    /** DESTINATIONS **/
    /******************/

    public static Location getDestination(String name) {
        if (!destinations.containsKey(name)) {
            return null;
        }
        return destinations.get(name);
    }

    public static void addDestination(String name, Location destination) {
        destinations.put(name, destination);
    }

    public static void removeDestination(String name) {
        destinations.remove(name);
    }

    public static Set<String> getDestinations() {
        return destinations.keySet();
    }

    public static void clearDestinations() {
        destinations.clear();
    }

    public static List<CustomPortal> getLinkedPortals(String destination) {
        final List<CustomPortal> portals = new ArrayList<CustomPortal>();
        for (PortalWorld portalWorld : portalWorlds.values()) {
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
        portalWorlds.put(world, portalWorld);
        return portalWorld;
    }

    public static PortalWorld getWorld(World world) {
        if (portalWorlds.containsKey(world)) {
            return portalWorlds.get(world);
        } else {
            return addWorld(world);
        }
    }

    public static Collection<PortalWorld> getPortalWorlds() {
        return portalWorlds.values();
    }

    public static void clearWorldPortals() {
        portalWorlds.clear();
    }

}
