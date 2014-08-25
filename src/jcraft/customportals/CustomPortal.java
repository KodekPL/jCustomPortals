package jcraft.customportals;

import static jcraft.customportals.CustomPortalsPlugin.DESTS_FILE;
import static jcraft.customportals.CustomPortalsPlugin.PORTALS_FILE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class CustomPortal {

    private final String name;
    private final PortalLocation location;
    private String destination;

    public CustomPortal(String name, PortalLocation location, String destination) {
        this.name = name.toLowerCase();
        this.location = location;
        this.destination = destination.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    public PortalLocation getLocation() {
        return this.location;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String name) {
        this.destination = name;
    }

    public void teleport(Player player) {
        if (CustomPortalsPlugin.checkPermissions && !player.hasPermission("jportal.teleport." + this.name)) {
            return;
        }
        final Location target = PortalWorld.getDestination(this.destination);
        final CustomPortalTeleportEvent event = new CustomPortalTeleportEvent(player, this, target);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (CustomPortalsPlugin.resetFalling) {
                player.setFallDistance(0);
            }
            player.teleport(event.getTarget(), TeleportCause.PLUGIN);
        }
    }

    /***************/
    /*** PORTALS ***/
    /***************/

    public static void loadPortals() {
        if (!PORTALS_FILE.exists()) return;
        PortalWorld.clearWorldPortals();

        final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(PORTALS_FILE);
        final Set<String> portals = ymlConfig.getConfigurationSection("Portals").getKeys(false);

        for (String name : portals) {
            String destName = ymlConfig.getString("Portals." + name + ".Destination");
            Location destLoc = getDestination(destName);
            if (destLoc == null) {
                Bukkit.getLogger().log(Level.WARNING, "Portal with name '" + name + "' is using non-existing destination!");
                continue;
            }

            World world = Bukkit.getWorld(ymlConfig.getString("Portals." + name + ".World"));
            if (world == null) {
                Bukkit.getLogger().log(Level.WARNING, "Portal with name '" + name + "' is using non-existing world!");
                continue;
            }

            String locData = ymlConfig.getString("Portals." + name + ".Location");
            PortalLocation location = PortalLocation.parseLocation(world, locData);
            if (location == null) {
                Bukkit.getLogger().log(Level.WARNING, "Portal with name '" + name + "' have incorrectly saved region!");
                continue;
            }

            addPortal(name, new CustomPortal(name, location, destName), false);
        }
    }

    public static void addPortal(String name, CustomPortal portal, boolean save) {
        final PortalWorld portalWorld = PortalWorld.getWorld(portal.getLocation().getWorld());
        portalWorld.addPortal(name, portal);

        if (save) {
            final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(PORTALS_FILE);
            ymlConfig.set("Portals." + portal.getName() + ".World", portal.getLocation().getWorld().getName());
            ymlConfig.set("Portals." + portal.getName() + ".Location", portal.getLocation().serializeLocation());
            ymlConfig.set("Portals." + portal.getName() + ".Destination", portal.getDestination());
            try {
                ymlConfig.save(PORTALS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removePortal(String name, World world) {
        name = name.toLowerCase();
        final PortalWorld portalWorld = PortalWorld.getWorld(world);
        portalWorld.removePortal(name);

        final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(PORTALS_FILE);
        ymlConfig.set("Portals." + name, null);
        try {
            ymlConfig.save(PORTALS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CustomPortal getPortal(String name) {
        name = name.toLowerCase();
        for (PortalWorld portalWorld : PortalWorld.getPortalWorlds()) {
            CustomPortal portal = portalWorld.getPortalByName(name);
            if (portal != null) {
                return portal;
            }
        }
        return null;
    }

    public static List<CustomPortal> getPortals() {
        final List<CustomPortal> portals = new ArrayList<CustomPortal>();
        for (PortalWorld portalWorld : PortalWorld.getPortalWorlds()) {
            portals.addAll(portalWorld.getPortals());
        }
        if (portals.isEmpty()) {
            return null;
        }
        return portals;
    }

    public static Collection<CustomPortal> getPortals(World world) {
        final PortalWorld portalWorld = PortalWorld.getWorld(world);
        final Collection<CustomPortal> portals = portalWorld.getPortals();
        if (portals.isEmpty()) {
            return null;
        }
        return portals;
    }

    /******************/
    /** DESTINATIONS **/
    /******************/

    public static void loadDestinations() {
        if (!DESTS_FILE.exists()) return;
        PortalWorld.clearDestinations();

        final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(DESTS_FILE);
        final Set<String> destinations = ymlConfig.getConfigurationSection("Destinations").getKeys(false);

        for (String name : destinations) {
            String[] locData = ymlConfig.getString("Destinations." + name + ".Location").split(",");
            if (locData.length < 6) {
                Bukkit.getLogger().log(Level.WARNING, "Destination with name '" + name + "' have incorrecly saved position!");
                continue;
            }
            World world = Bukkit.getWorld(locData[0]);
            if (world == null) {
                Bukkit.getLogger().log(Level.WARNING, "Destination with name '" + name + "' is using non-existing world!");
                continue;
            }
            Location location = null;

            try {
                location = new Location(world, Double.parseDouble(locData[1]), Double.parseDouble(locData[2]), Double.parseDouble(locData[3]),
                        Float.parseFloat(locData[4]), Float.parseFloat(locData[5]));
            } catch (NumberFormatException e) {
                Bukkit.getLogger().log(Level.WARNING, "Destination with name '" + name + "' have incorrecly saved position!");
                continue;
            }

            addDestination(name, location, false);
        }
    }

    public static void addDestination(String name, Location dest, boolean save) {
        PortalWorld.addDestination(name.toLowerCase(), dest);

        if (save) {
            final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(DESTS_FILE);
            ymlConfig.set("Destinations." + name + ".Location", dest.getWorld().getName() + "," + dest.getX() + "," + dest.getY() + "," + dest.getZ()
                    + "," + dest.getYaw() + "," + dest.getPitch());
            try {
                ymlConfig.save(DESTS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeDestination(String name) {
        name = name.toLowerCase();
        PortalWorld.removeDestination(name);

        final YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(DESTS_FILE);
        ymlConfig.set("Destinations." + name, null);
        try {
            ymlConfig.save(DESTS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Location getDestination(String name) {
        name = name.toLowerCase();
        return PortalWorld.getDestination(name);
    }

    public static Set<String> getDestinations() {
        final Set<String> destinations = PortalWorld.getDestinations();
        if (destinations.isEmpty()) {
            return null;
        }
        return destinations;
    }

}
