package jcraft.customportals;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandsHandler implements CommandExecutor {

    private final CustomPortalsPlugin plugin;

    public CommandsHandler(CustomPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (args.length == 0) {
            final String jportal = ChatColor.GOLD + "  /jportal " + ChatColor.YELLOW;
            player.sendMessage(new String[] {
                    jportal + "create destination [DESTINATION NAME]" + ChatColor.BLUE
                            + " - Creates teleport destination with specified name on player position.",
                    jportal + "create portal [PORTAL NAME] [DESTINATION NAME]" + ChatColor.BLUE
                            + " - Creates portal with the specified name to destination with specified name.",
                    jportal + "info destination [DESTINATION NAME]" + ChatColor.BLUE + " - Displays informations about destintion of specified name.",
                    jportal + "info portal [PORTAL NAME]" + ChatColor.BLUE + " - Displays informations about destintion of specified name.",
                    jportal + "delete destination [DESTINATION NAME]" + ChatColor.BLUE + " - Removes portal destination of specified name.",
                    jportal + "delete portal [PORTAL NAME]" + ChatColor.BLUE + " - Removes portal of specified name.",
                    jportal + "modify destination [DESTINATION NAME]" + ChatColor.BLUE
                            + " - Changes destination position of specified name to player position.",
                    jportal + "modify portal destination [PORTAL NAME] [DESTINATION NAME]" + ChatColor.BLUE
                            + " - Changes destination of specified portal name.",
                    jportal + "modify portal location [PORTAL NAME]" + ChatColor.BLUE + " - Changes portal shape of specified name.",
                    jportal + "teleport [DESTINATION NAME]" + ChatColor.BLUE + " - Teleports to destination of specified name.",
                    jportal + "list portal" + ChatColor.BLUE + " - Displays list of all portal names.",
                    jportal + "list destination" + ChatColor.BLUE + " - Displays list of all destination names.",
                    jportal + "reload" + ChatColor.BLUE + " - Reloads config file, portal list and destination list." });
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadPluginCmd(player, "jportal.admin", args);
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("teleport")) {
                teleportDestCmd(player, "jportal.teleportdest", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("destination")) {
                listDestCmd(player, "jportal.listdest", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("portal")) {
                listPortalCmd(player, "jportal.listportal", args);
                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("destination")) {
                createDestinationCmd(player, "jportal.createdest", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("info") && args[1].equalsIgnoreCase("portal")) {
                infoPortalCmd(player, "jportal.infoportal", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("info") && args[1].equalsIgnoreCase("destination")) {
                infoDestinationCmd(player, "jportal.infodest", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("portal")) {
                deletePortalCmd(player, "jportal.deleteportal", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("destination")) {
                deleteDestinationCmd(player, "jportal.deletedest", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("modify") && args[1].equalsIgnoreCase("destination")) {
                modifyDestinationCmd(player, "jportal.modifydest", args);
                return true;
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("portal")) {
                createPortalCmd(player, "jportal.createportal", args);
                return true;
            }
            if (args[0].equalsIgnoreCase("modify") && args[1].equalsIgnoreCase("portal") && args[2].equalsIgnoreCase("location")) {
                modifyPortalLocationCmd(player, "jportal.modifyportal", args);
                return true;
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("modify") && args[1].equalsIgnoreCase("portal") && args[2].equalsIgnoreCase("destination")) {
                modifyPortalDestCmd(player, "jportal.modifyportal", args);
                return true;
            }
        }

        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Command was not found!");
        return false;
    }

    private boolean hasPermission(Player player, String permission) {
        if (player == null) return false;
        if (!player.hasPermission(permission)) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "No permission!");
            return false;
        }
        return true;
    }

    /**********/
    /** CELE **/
    /**********/

    public void createDestinationCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String destinationName = args[2].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination != null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name already exists!");
            return;
        }

        CustomPortal.addDestination(destinationName, player.getEyeLocation().subtract(0, 1, 0), true);
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Destination with name '" + destinationName
                + "' has been created and saved!");
    }

    public void infoDestinationCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String destinationName = args[2].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name does not exists!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "=== Destination " + destinationName + " ===");
        player.sendMessage(ChatColor.GRAY + "  World >> " + destination.getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "  Position >> X: " + destination.getX() + ", Y: " + destination.getY() + ", Z: " + destination.getZ());

        final List<CustomPortal> linkedPortals = PortalWorld.getLinkedPortals(destinationName);
        final StringBuilder sLinked = new StringBuilder();
        boolean color = true;
        for (CustomPortal portal : linkedPortals) {
            if (color) {
                sLinked.append(ChatColor.YELLOW).append(portal.getName()).append(ChatColor.WHITE).append(", ");
                color = false;
            } else {
                sLinked.append(portal.getName()).append(", ");
                color = true;
            }
        }
        player.sendMessage(ChatColor.GRAY + "  Linked portals >> " + sLinked.toString());

        player.sendMessage(ChatColor.YELLOW + "======");
    }

    public void listDestCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final StringBuilder sDest = new StringBuilder();
        boolean color = false;
        final Set<String> destinations = CustomPortal.getDestinations();
        for (String name : destinations) {
            if (color) {
                sDest.append(ChatColor.YELLOW).append(name).append(ChatColor.WHITE).append(", ");
                color = false;
            } else {
                sDest.append(name).append(", ");
                color = true;
            }
        }
        player.sendMessage(ChatColor.GRAY + "Existing destinations: " + ChatColor.WHITE + sDest.toString());
    }

    public void modifyDestinationCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String destinationName = args[2].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name does not exists!");
            return;
        }

        CustomPortal.addDestination(destinationName, player.getEyeLocation().subtract(0, 1, 0), true);
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Destination with name '" + destinationName
                + "' has been modified and saved!");
    }

    public void deleteDestinationCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String destinationName = args[2].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name does not exists!");
            return;
        }

        final List<CustomPortal> linkedPortals = PortalWorld.getLinkedPortals(destinationName);
        if (!linkedPortals.isEmpty()) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "There are portals using this destination!");
            return;
        }

        CustomPortal.removeDestination(destinationName);
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Destination with name '" + destinationName + "' has been removed!");
    }

    public void teleportDestCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String destinationName = args[1].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name does not exists!");
            return;
        }

        player.teleport(destination);
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Teleported to destination with name '" + destinationName + "'!");
    }

    /***************/
    /*** PORTALE ***/
    /***************/

    public void createPortalCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String portalName = args[2].toLowerCase();
        final CustomPortal portal = CustomPortal.getPortal(portalName);
        if (portal != null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Portal with this name already exists!");
            return;
        }

        final String destinationName = args[3].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name does not exists!");
            return;
        }

        if (!CustomPortalsPlugin.getWorldEdit().hasSelected(player)) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "You need to first select region with WorldEdit to create portal!");
            return;
        }

        final Selection selection = CustomPortalsPlugin.getWorldEdit().getSelection(player);
        if (selection instanceof CuboidSelection) {
            final Location loc1 = selection.getMinimumPoint();
            final Location loc2 = selection.getMaximumPoint();

            final PortalLocation pLoc = new PortalLocation(selection.getWorld(), new Vector(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()),
                    new Vector(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
            CustomPortal.addPortal(portalName, new CustomPortal(portalName, pLoc, destinationName), true);
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Portal with name '" + portalName + "' has been created and saved!");
        } else {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Selection type need to be cuboid!");
        }
    }

    public void infoPortalCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final CustomPortal portal = CustomPortal.getPortal(args[2]);
        if (portal == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Portal with this name does not exists!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "=== Portal " + portal.getName() + " ===");
        player.sendMessage(ChatColor.GRAY + "  World >> " + portal.getLocation().getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "  Region position >> " + portal.getLocation().serializeLocation());
        player.sendMessage(ChatColor.GRAY + "  Destination >> " + portal.getDestination());
        player.sendMessage(ChatColor.YELLOW + "======");
    }

    public void listPortalCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final StringBuilder sPortal = new StringBuilder();
        boolean color = false;
        final List<CustomPortal> portals = CustomPortal.getPortals();
        for (CustomPortal portal : portals) {
            if (color) {
                sPortal.append(ChatColor.YELLOW).append(portal.getName()).append(ChatColor.WHITE).append(", ");
                color = false;
            } else {
                sPortal.append(portal.getName()).append(", ");
                color = true;
            }
        }
        player.sendMessage(ChatColor.GRAY + "Existing portal names: " + ChatColor.WHITE + sPortal.toString());
    }

    public void modifyPortalLocationCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String portalName = args[3].toLowerCase();
        final CustomPortal portal = CustomPortal.getPortal(portalName);
        if (portal == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Portal with this name does not exists!");
            return;
        }

        if (!CustomPortalsPlugin.getWorldEdit().hasSelected(player)) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "You need to first select region with WorldEdit to modify portal!");
            return;
        }

        final Selection selection = CustomPortalsPlugin.getWorldEdit().getSelection(player);
        if (selection instanceof CuboidSelection) {
            final Location loc1 = selection.getMinimumPoint();
            final Location loc2 = selection.getMaximumPoint();

            final PortalLocation pLoc = new PortalLocation(selection.getWorld(), new Vector(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()),
                    new Vector(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
            CustomPortal.removePortal(portal.getName(), portal.getLocation().getWorld());
            CustomPortal.addPortal(portal.getName(), new CustomPortal(portal.getName(), pLoc, portal.getDestination()), true);
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Portal with name '" + portalName + "' has been modified and saved!");
        } else {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Selection type need to be cuboid!");
        }
    }

    public void modifyPortalDestCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String portalName = args[3].toLowerCase();
        final CustomPortal portal = CustomPortal.getPortal(portalName);
        if (portal == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Portal with this name does not exists!");
            return;
        }

        final String destinationName = args[4].toLowerCase();
        final Location destination = CustomPortal.getDestination(destinationName);
        if (destination == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Destination with this name does not exists!");
            return;
        }

        portal.setDestination(destinationName);
        CustomPortal.addPortal(portal.getName(), portal, true);
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Portal with name '" + portal.getName() + "' has been modified and saved!");
    }

    public void deletePortalCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        final String portalName = args[2].toLowerCase();
        final CustomPortal portal = CustomPortal.getPortal(portalName);
        if (portal == null) {
            player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.RED + "Portal with this name does not exists!");
            return;
        }

        CustomPortal.removePortal(portalName, portal.getLocation().getWorld());
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Portal with name '" + portalName + "' has been removed!");
    }

    /************/
    /*** INNE ***/
    /************/

    public void reloadPluginCmd(Player player, String permission, String[] args) {
        if (!hasPermission(player, permission)) return;

        plugin.loadConfig();
        CustomPortal.loadDestinations();
        CustomPortal.loadPortals();
        player.sendMessage(CustomPortalsPlugin.PREFIX + ChatColor.GREEN + "Config file, destination list and portal list has been reloaded!");
    }

}
